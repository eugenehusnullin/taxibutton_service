package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb.dao.IOfferedOrderBrokerDao;
import tb.domain.order.OfferedOrderBroker;
import tb.domain.order.Order;

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
