package tb.car.dao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.car.domain.Car;
import tb.car.domain.CarState;
import tb.car.domain.CarStateEnum;
import tb.domain.Broker;

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
				savedCarState.setLatitude(carState.getLatitude());
				savedCarState.setLongitude(carState.getLongitude());
				savedCarState.setDate(carState.getDate());
				session.update(savedCarState);
			}
		}
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
			carState.setState(carStateEnum);
			session.update(carState);

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
	public List<CarState> getNearCarStates(double lat, double lon, double diff, List<Long> limitBrokerIds) {
		Session session = sessionFactory.getCurrentSession();
		Date date = new Date((new Date()).getTime() - 300000);
		String q = " from CarState cs "
				+ " where cs.state=0 and cs.date>=:date "
				+ " and (abs(:lat-cs.latitude) + abs(:lon-cs.longitude)) <= :diff "				
				+ " order by abs(:lat-cs.latitude) + abs(:lon-cs.longitude) ";

		@SuppressWarnings("unchecked")
		List<CarState> list = (List<CarState>) session.createQuery(q)
				.setDate("date", date)
				.setDouble("lat", lat)
				.setDouble("lon", lon)
				.setDouble("diff", diff)
				.list();

		if (limitBrokerIds != null && limitBrokerIds.size() > 0) {
			list = list.stream()
					.filter(p -> limitBrokerIds.contains(p.getBrokerId()))
					.collect(Collectors.toList());
		}

		return list;
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
}
