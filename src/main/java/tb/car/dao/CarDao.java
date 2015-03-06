package tb.car.dao;

import java.util.Date;
import java.util.List;

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
					.add(Restrictions.eq("brokerId", broker.getId())).add(Restrictions.eq("uuid", car.getUuid()))
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
}
