package tb2014.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOrderCancelDao;
import tb2014.domain.order.OrderCancel;

@Repository("OrderCancelDao")
public class OrderCancelDao implements IOrderCancelDao {

	private SessionFactory sessionFactory;

	@Autowired
	public OrderCancelDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void save(OrderCancel orderCancel) {
		sessionFactory.getCurrentSession().save(orderCancel);
	}
}
