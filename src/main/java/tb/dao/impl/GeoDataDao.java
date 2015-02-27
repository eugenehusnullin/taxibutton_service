package tb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IGeoDataDao;
import tb.domain.order.GeoData;
import tb.domain.order.Order;

@Repository("GeoDataDao")
public class GeoDataDao implements IGeoDataDao {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

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
