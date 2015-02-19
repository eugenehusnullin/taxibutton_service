package tb.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb.dao.IMapAreaDao;
import tb.domain.maparea.MapArea;

@Repository("MapAreaDao")
public class MapAreaDao implements IMapAreaDao {

	private SessionFactory sessionFactory;

	@Autowired
	public MapAreaDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void add(MapArea mapArea) {
		sessionFactory.getCurrentSession().save(mapArea);
	}

}
