package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOrderStatusDao;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

@Repository("OrderStatusDao")
public class OrderStatusDao implements IOrderStatusDao {

	private SessionFactory sessionFactory;

	@Autowired
	public OrderStatusDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

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
