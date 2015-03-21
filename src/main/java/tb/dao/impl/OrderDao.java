package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IOrderDao;
import tb.domain.order.Feedback;
import tb.domain.order.Order;

@Repository("OrderDao")
public class OrderDao implements IOrderDao {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public Order get(Long id) {
		return (Order) sessionFactory.getCurrentSession().get(Order.class, id);
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
	public Order get(String uuid) {
		return (Order) sessionFactory.getCurrentSession().createCriteria(Order.class)
				.add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> getPagination(String orderField, String orderDirection, int start, int count) {

		org.hibernate.criterion.Order orderBy;
		if (orderDirection.equals("asc")) {
			orderBy = org.hibernate.criterion.Order.asc(orderField);
		} else {
			orderBy = org.hibernate.criterion.Order.desc(orderField);
		}

		return sessionFactory.getCurrentSession()
				.createCriteria(Order.class)
				.addOrder(orderBy)
				.setFirstResult(start)
				.setMaxResults(count)
				.list();
	}

	@Override
	public Long getAllOrdersCount() {
		return (Long) sessionFactory.getCurrentSession().createCriteria(Order.class)
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	@Override
	public void saveFeedback(Feedback feedback) {
		sessionFactory.getCurrentSession().save(feedback);
	}

}
