package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
		for (MapArea mapArea : broker.getMapAreas()) {
			sessionFactory.getCurrentSession().delete(mapArea);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<MapArea> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(MapArea.class).list();
	}

}
