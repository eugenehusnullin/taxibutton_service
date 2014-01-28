package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOrderDao;
import tb2014.domain.order.Order;

@Repository("OrderDao")
public class OrderDao implements IOrderDao {

	private SessionFactory sessionFactory;

	@Autowired
	public OrderDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Order get(Long id) {
		return (Order) sessionFactory.getCurrentSession().get(Order.class, id);
	}

	@Override
	public Order getWithChilds(Long id) {
		return (Order) sessionFactory.getCurrentSession()
				.createCriteria(Order.class)
				.add(Restrictions.eq("id", id))
				.setFetchMode("destinations", FetchMode.JOIN)
				.setFetchMode("requirements", FetchMode.JOIN)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(Order.class)
				.list();
	}

	@Override
	public void save(Order order) {
		sessionFactory.getCurrentSession().save(order);
	}

	@Override
	public void saveOrUpdate(Order order) {
		sessionFactory.getCurrentSession().saveOrUpdate(order);
	}

}
