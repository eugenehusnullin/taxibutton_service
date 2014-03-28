package tb2014.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IGeoDataDao;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;

@Repository("GeoDataDao")
public class GeoDataDao implements IGeoDataDao {

	private SessionFactory sessionFactory;

	@Autowired
	public GeoDataDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoData> getAll(Order order) {
		return sessionFactory.getCurrentSession().createCriteria(GeoData.class).add(Restrictions.eq("order", order))
				.list();
	}

	@Override
	public void save(GeoData geoData) {
		sessionFactory.getCurrentSession().save(geoData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoData> getAll(Order order, Date date) {
		return sessionFactory.getCurrentSession().createCriteria(GeoData.class).add(Restrictions.eq("order", order))
				.add(Restrictions.ge("date", date)).list();
	}

}
