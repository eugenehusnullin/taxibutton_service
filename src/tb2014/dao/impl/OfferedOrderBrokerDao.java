package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOfferedOrderBrokerDao;
import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;

@Repository("OfferedOrderBrokerDao")
public class OfferedOrderBrokerDao implements IOfferedOrderBrokerDao {
	
	private SessionFactory sessionFactory;
	
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
			.add(Restrictions.eq("order", order))
			.list();
	}

}
