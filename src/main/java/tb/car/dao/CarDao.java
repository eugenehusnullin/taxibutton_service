package tb.car.dao;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

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
	@Qualifier("inmemorydbSessionFactory")
	private SessionFactory sessionFactory;
	
	@Transactional
	public void updateCars(Map<String, Car> cars, Broker broker, Date loadDate) {
		Session session = sessionFactory.getCurrentSession();
		
		for (Entry<String, Car> entry : cars.entrySet()) {
			session.saveOrUpdate(entry.getValue());
			
			CarState carState = (CarState) session.createCriteria(CarState.class)
				.add(Restrictions.eq("brokerId", broker.getId()))
				.add(Restrictions.eq("uuid", entry.getValue().getUuid()))
				.uniqueResult();
			
			if (carState == null) {
				carState = new CarState();
				carState.setBrokerId(broker.getId());
				carState.setUuid(entry.getValue().getUuid());
				carState.setState(CarStateEnum.Undefined);
				carState.setLatitude(0);
				carState.setLongitude(0);
				carState.setDate(loadDate);
				
				session.save(carState);
			}
		}
	}
}
