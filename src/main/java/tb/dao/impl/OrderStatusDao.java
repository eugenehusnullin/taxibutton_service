package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IOrderStatusDao;
import tb.domain.order.Order;
import tb.domain.order.OrderStatus;

@Repository("OrderStatusDao")
public class OrderStatusDao implements IOrderStatusDao {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderStatus> get(Order order) {
		return sessionFactory.getCurrentSession()
				.createCriteria(OrderStatus.class)
				.add(Restrictions.eq("order", order)).list();
	}

	@Override
	public void save(OrderStatus orderStatus) {
		sessionFactory.getCurrentSession().save(orderStatus);
	}

	@Override
	public OrderStatus getLast(Order order) {

		OrderStatus lastStatus = (OrderStatus) sessionFactory.getCurrentSession()
				.createCriteria(OrderStatus.class)
				.add(Restrictions.eq("order", order))
				.addOrder(org.hibernate.criterion.Order.desc("date"))
				.setMaxResults(1).uniqueResult();

		return lastStatus;
	}
}
