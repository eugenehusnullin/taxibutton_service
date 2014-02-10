package tb2014.dao.impl;

import java.util.List;

import org.hibernate.FetchMode;
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

	@Override
	public OrderStatus get(Long id) {
		return (OrderStatus) sessionFactory.getCurrentSession().get(
				OrderStatus.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderStatus> get(Order order) {
		return sessionFactory.getCurrentSession()
				.createCriteria(OrderStatus.class)
				.add(Restrictions.eq("order", order)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderStatus> getAll() {
		return sessionFactory.getCurrentSession()
				.createCriteria(OrderStatus.class).list();
	}

	@Override
	public void save(OrderStatus orderStatus) {
		sessionFactory.getCurrentSession().save(orderStatus);
	}

	@Override
	public void saveOrUpdate(OrderStatus orderStatus) {
		sessionFactory.getCurrentSession().saveOrUpdate(orderStatus);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrderStatus getLast(Order order) {

		List<OrderStatus> statusList = sessionFactory.getCurrentSession()
				.createCriteria(OrderStatus.class)
				.addOrder(org.hibernate.criterion.Order.desc("date")).list();

		if (statusList.size() != 0) {
			return statusList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrderStatus getLastWithChilds(Order order) {

		List<OrderStatus> statusList = sessionFactory.getCurrentSession()
				.createCriteria(OrderStatus.class)
				.addOrder(org.hibernate.criterion.Order.desc("date"))
				.setFetchMode("order", FetchMode.JOIN).list();

		if (statusList.size() != 0) {
			return statusList.get(0);
		} else {
			return null;
		}
	}
}
