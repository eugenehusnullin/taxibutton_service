package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IMapAreaDao;
import tb.domain.Broker;
import tb.domain.maparea.MapArea;

@Repository("MapAreaDao")
public class MapAreaDao implements IMapAreaDao {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public void add(MapArea mapArea) {
		sessionFactory.getCurrentSession().save(mapArea);
	}

	@Override
	public void delete(Broker broker) {
		String hqlDelete = "delete MapArea where :broker in elements(brokers)";
		sessionFactory.getCurrentSession().createQuery(hqlDelete)
				.setEntity("broker", broker)
				.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MapArea> get(Broker broker) {
		String hqlDelete = "from MapArea where :broker in elements(brokers)";
		return sessionFactory.getCurrentSession().createQuery(hqlDelete)
				.setEntity("broker", broker)
				.list();
	}

}
