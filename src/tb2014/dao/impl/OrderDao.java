package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOrderDao;
import tb2014.domain.Device;
import tb2014.domain.order.AddressPoint;
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
				.createCriteria(Order.class).add(Restrictions.eq("id", id))
				.setFetchMode("destinations", FetchMode.JOIN)
				.setFetchMode("requirements", FetchMode.JOIN)
				.setFetchMode("broker", FetchMode.JOIN)
				.setFetchMode("device", FetchMode.JOIN).uniqueResult();
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

	@Override
	public void delete(Order order) {
		sessionFactory.getCurrentSession().delete(order);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> getAll(Device device) {
		return sessionFactory.getCurrentSession().createCriteria(Order.class)
				.add(Restrictions.eq("device", device)).list();
	}

	@Override
	public Order get(String uuid) {
		return (Order) sessionFactory.getCurrentSession()
				.createCriteria(Order.class).add(Restrictions.eq("uuid", uuid))
				.uniqueResult();
	}

	@Override
	public Order getWithChilds(String uuid) {

		return (Order) sessionFactory.getCurrentSession()
				.createCriteria(Order.class).add(Restrictions.eq("uuid", uuid))
				.setFetchMode("broker", FetchMode.JOIN)
				.setFetchMode("device", FetchMode.JOIN).uniqueResult();
	}

	@Override
	public AddressPoint getSourcePoint(Order order) {

		return (AddressPoint) sessionFactory.getCurrentSession()
				.createCriteria(AddressPoint.class)
				.add(Restrictions.eq("order", order))
				.add(Restrictions.eq("indexNumber", 0)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> getAllWithChilds() {
		return sessionFactory.getCurrentSession().createCriteria(Order.class)
				.setFetchMode("broker", FetchMode.JOIN).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> getAllWithParams(String orderField,
			String orderDirection, int start, int count) {

		if (orderDirection.equals("asc")) {

			if (orderField.equals("supplyDate")) {
				return sessionFactory
						.getCurrentSession()
						.createCriteria(Order.class)
						.addOrder(org.hibernate.criterion.Order.asc(orderField))
						.addOrder(
								org.hibernate.criterion.Order.asc("supplyHour"))
						.addOrder(
								org.hibernate.criterion.Order.asc("supplyMin"))
						.setFirstResult(start).setMaxResults(count)
						.setFetchMode("broker", FetchMode.JOIN).list();
			} else {
				return sessionFactory
						.getCurrentSession()
						.createCriteria(Order.class)
						.addOrder(org.hibernate.criterion.Order.asc(orderField))
						.setFirstResult(start).setMaxResults(count)
						.setFetchMode("broker", FetchMode.JOIN).list();
			}
		} else {

			if (orderField.equals("supplyDate")) {
				return sessionFactory
						.getCurrentSession()
						.createCriteria(Order.class)
						.addOrder(
								org.hibernate.criterion.Order.desc(orderField))
						.addOrder(
								org.hibernate.criterion.Order
										.desc("supplyHour"))
						.addOrder(
								org.hibernate.criterion.Order.desc("supplyMin"))
						.setFirstResult(start).setMaxResults(count)
						.setFetchMode("broker", FetchMode.JOIN).list();
			} else {
				return sessionFactory
						.getCurrentSession()
						.createCriteria(Order.class)
						.addOrder(
								org.hibernate.criterion.Order.desc(orderField))
						.setFirstResult(start).setMaxResults(count)
						.setFetchMode("broker", FetchMode.JOIN).list();
			}
		}
	}

	@Override
	public Long getAllOrdersCount() {
		return (Long) sessionFactory.getCurrentSession()
				.createCriteria(Order.class)
				.setProjection(Projections.rowCount()).uniqueResult();
	}

}
