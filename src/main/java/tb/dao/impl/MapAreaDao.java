package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
	@Transactional
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

	@Override
	@Transactional
	public void delete(Long id) {
		String delete = "delete MapArea where id = :id";
		sessionFactory.getCurrentSession().createQuery(delete)
				.setLong("id", id)
				.executeUpdate();
	}

	@Override
	@Transactional
	public MapArea get(Long id) {
		return (MapArea) sessionFactory.getCurrentSession()
				.createCriteria(MapArea.class)
				.add(Restrictions.idEq(id))
				.uniqueResult();
	}

}
