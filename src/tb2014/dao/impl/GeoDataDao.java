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

	@Override
	public GeoData get(Long id) {
		return (GeoData) sessionFactory.getCurrentSession().get(GeoData.class,
				id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoData> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(GeoData.class)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoData> getAll(Order order) {
		return sessionFactory.getCurrentSession().createCriteria(GeoData.class)
				.add(Restrictions.eq("order", order)).list();
	}

	@Override
	public GeoData getLast(Order order) {
		return (GeoData) sessionFactory.getCurrentSession()
				.createCriteria(GeoData.class)
				.addOrder(org.hibernate.criterion.Order.desc("data")).list()
				.get(0);
	}

	@Override
	public void save(GeoData geoData) {
		sessionFactory.getCurrentSession().save(geoData);
	}

	@Override
	public void saveOrUpdate(GeoData geoData) {
		sessionFactory.getCurrentSession().saveOrUpdate(geoData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoData> getAll(Order order, Date date) {
		return sessionFactory.getCurrentSession().createCriteria(GeoData.class)
				.add(Restrictions.gt("date", date)).list();
	}

}
