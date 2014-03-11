package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOfferedOrderBrokerDao;
import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;

@Repository("OfferedOrderBrokerDao")
public class OfferedOrderBrokerDao implements IOfferedOrderBrokerDao {

	private SessionFactory sessionFactory;

	@Autowired
	public OfferedOrderBrokerDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void save(OfferedOrderBroker offeredOrderBroker) {
		sessionFactory.getCurrentSession().save(offeredOrderBroker);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OfferedOrderBroker> get(Order order) {
		return sessionFactory.getCurrentSession().createCriteria(OfferedOrderBroker.class)
				.add(Restrictions.eq("order", order)).list();
	}

	@Override
	public Long count(Order order) {
		return (Long) sessionFactory.getCurrentSession().createCriteria(OfferedOrderBroker.class)
				.add(Restrictions.eq("order", order)).setProjection(Projections.rowCount()).uniqueResult();
	}

}
