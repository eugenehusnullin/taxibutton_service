package tb.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IOrderCancelDao;
import tb.domain.order.OrderCancel;

@Repository("OrderCancelDao")
public class OrderCancelDao implements IOrderCancelDao {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public void save(OrderCancel orderCancel) {
		sessionFactory.getCurrentSession().save(orderCancel);
	}
}
