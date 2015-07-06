package tb.car.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.car.domain.Car;
import tb.car.domain.CarState;
import tb.car.domain.CarStateEnum;
import tb.car.domain.GeoData;
import tb.domain.Broker;
import tb.domain.order.Requirement;

@Service
public class CarDao {

	@Autowired
	@Qualifier("inmemDbSf")
	private SessionFactory sessionFactory;

	@Transactional(value = "inmemDbTm")
	public void updateCars(List<Car> cars, Broker broker, Date loadDate) {
		Session session = sessionFactory.getCurrentSession();

		for (Car car : cars) {
			session.saveOrUpdate(car);

			CarState carState = (CarState) session.createCriteria(CarState.class)
					.add(Restrictions.eq("brokerId", broker.getId()))
					.add(Restrictions.eq("uuid", car.getUuid()))
					.uniqueResult();

			if (carState == null) {
				carState = new CarState();
				carState.setBrokerId(broker.getId());
				carState.setUuid(car.getUuid());
				carState.setState(CarStateEnum.Undefined);
				carState.setLatitude(0);
				carState.setLongitude(0);
				carState.setDate(loadDate);

				session.save(carState);
			}
		}
	}

	@Transactional(value = "inmemDbTm")
	public void updateCarStateGeos(Broker broker, List<CarState> carStates) {
		// TODO: bulk update
		Session session = sessionFactory.getCurrentSession();
		for (CarState carState : carStates) {
			CarState savedCarState = (CarState) session.createCriteria(CarState.class)
					.add(Restrictions.eq("brokerId", broker.getId()))
					.add(Restrictions.eq("uuid", carState.getUuid()))
					.uniqueResult();

			if (savedCarState != null) {
				String update = "update CarState c set c.latitude = :latitude, c.longitude = :longitude"
						+ ", c.date = :date "
						+ "where c.brokerId = :brokerId and c.uuid = :uuid";
				session.createQuery(update)
						.setDouble("latitude", carState.getLatitude())
						.setDouble("longitude", carState.getLongitude())
						.setTimestamp("date", carState.getDate())
						.setLong("brokerId", savedCarState.getBrokerId())
						.setString("uuid", savedCarState.getUuid())
						.executeUpdate();
			}

			GeoData geoData = createGeoData(broker, carState);
			session.save(geoData);
		}
	}

	private GeoData createGeoData(Broker broker, CarState carState) {
		GeoData geoData = new GeoData();
		geoData.setBrokerId(broker.getId());
		geoData.setUuid(carState.getUuid());
		geoData.setDate(carState.getDate());
		geoData.setLat(carState.getLatitude());
		geoData.setLon(carState.getLongitude());

		return geoData;
	}

	@Transactional(value = "inmemDbTm")
	public boolean updateCarStateStatus(Broker broker, String uuid, CarStateEnum carStateEnum) {
		Session session = sessionFactory.getCurrentSession();

		CarState carState = (CarState) session.createCriteria(CarState.class)
				.add(Restrictions.eq("brokerId", broker.getId()))
				.add(Restrictions.eq("uuid", uuid))
				.uniqueResult();

		if (carState == null) {
			return false;
		} else {
			String update = "update CarState c set c.state = :state "
					+ " where c.brokerId = :brokerId and c.uuid = :uuid";
			session.createQuery(update)
					.setParameter("state", carStateEnum)
					.setLong("brokerId", carState.getBrokerId())
					.setString("uuid", carState.getUuid())
					.executeUpdate();

			return true;
		}
	}

	@Transactional(value = "inmemDbTm")
	public void updateCarStateStatuses(Broker broker, List<CarState> carStates) {
		// TODO: bulk update
		for (CarState carState : carStates) {
			updateCarStateStatus(broker, carState.getUuid(), carState.getState());
		}
	}

	@Transactional(value = "inmemDbTm")
	public List<CarState> getNearCarStates(double lat, double lon, double diff) {
		Session session = sessionFactory.getCurrentSession();
		Date date = new Date((new Date()).getTime() - 300000);
		String q = " from CarState cs "
				+ " where cs.state=0 "
				+ " and cs.date>=:date "
				+ " and (abs(:lat-cs.latitude) + abs(:lon-cs.longitude)) <= :diff "
				+ " order by abs(:lat-cs.latitude) + abs(:lon-cs.longitude) ";

		@SuppressWarnings("unchecked")
		List<CarState> list = (List<CarState>) session.createQuery(q)
				.setTimestamp("date", date)
				.setDouble("lat", lat)
				.setDouble("lon", lon)
				.setDouble("diff", diff)
				.list();

		return list;
	}

	@Transactional(value = "inmemDbTm")
	public List<?> getCarsWithCarStates(Long brokerId) {
		Session session = sessionFactory.getCurrentSession();
		String q = " from CarState a, Car b"
				+ " where a.brokerId = b.brokerId "
				+ " and a.brokerId = :brokerId "
				+ " and a.uuid = b.uuid "
				+ " order by a.date desc ";

		List<?> list = session.createQuery(q)
				.setLong("brokerId", brokerId)
				.list();

		return list;
	}

	@Transactional(value = "inmemDbTm")
	public List<CarState> getCarStatesByRequirements(List<CarState> carStates, Set<Requirement> reqs) {
		if (reqs == null || reqs.size() == 0) {
			return carStates;
		}

		List<String> reqsKeys = reqs.stream().map(p -> p.getType()).collect(Collectors.toList());
		Session session = sessionFactory.getCurrentSession();
		List<CarState> filteredCarStates = new ArrayList<CarState>();
		for (CarState carState : carStates) {
			Car car = (Car) session.createCriteria(Car.class)
					.add(Restrictions.eq("brokerId", carState.getBrokerId()))
					.add(Restrictions.eq("uuid", carState.getUuid()))
					.uniqueResult();

			boolean b = reqsKeys.stream().allMatch(p -> car.getCarRequires().containsKey(p) 
					&& !car.getCarRequires().get(p).equals("no"));
			if (b) {
				filteredCarStates.add(carState);
			}
		}
		return filteredCarStates;
	}

	@Transactional(value = "inmemDbTm")
	public String getFirstTariff(Long brokerId, String uuid) {
		Session session = sessionFactory.getCurrentSession();

		Car car = (Car) session.createCriteria(Car.class)
				.add(Restrictions.eq("brokerId", brokerId))
				.add(Restrictions.eq("uuid", uuid))
				.uniqueResult();

		if (car != null) {
			return car.getTariffs().get(0);
		} else {
			return null;
		}
	}

	@Transactional(value = "inmemDbTm")
	public Car getCar(Long brokerId, String uuid) {
		Session session = sessionFactory.getCurrentSession();

		return (Car) session.createCriteria(Car.class)
				.add(Restrictions.eq("brokerId", brokerId))
				.add(Restrictions.eq("uuid", uuid))
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(value = "inmemDbTm")
	public List<GeoData> getGeoData(Long brokerId, String carUuid, Date date) {
		return sessionFactory.getCurrentSession()
				.createCriteria(GeoData.class)
				.add(Restrictions.eq("brokerId", brokerId))
				.add(Restrictions.eq("uuid", carUuid))
				.add(Restrictions.ge("date", date))
				.addOrder(Order.asc("date"))
				.list();
	}
}
