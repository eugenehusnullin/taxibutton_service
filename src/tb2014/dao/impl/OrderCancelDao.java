package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOrderCancelDao;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderCancel;

@Repository("OrderCancelDao")
public class OrderCancelDao implements IOrderCancelDao {

	private SessionFactory sessionFactory;

	@Autowired
	public OrderCancelDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public OrderCancel get(Long id) {
		return (OrderCancel) sessionFactory.getCurrentSession().get(
				OrderCancel.class, id);
	}

	@Override
	public OrderCancel get(Order order) {
		return (OrderCancel) sessionFactory.getCurrentSession()
				.createCriteria(OrderCancel.class)
				.add(Restrictions.eq("order", order)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderCancel> getAll() {
		return sessionFactory.getCurrentSession()
				.createCriteria(OrderCancel.class).list();
	}

	@Override
	public void save(OrderCancel orderCancel) {
		sessionFactory.getCurrentSession().save(orderCancel);
	}

	@Override
	public void saveOrUpdate(OrderCancel orderCancel) {
		sessionFactory.getCurrentSession().saveOrUpdate(orderCancel);
	}
}
