package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IOrderAcceptAlacrityDao;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;

@Repository("OrderAcceptAlacrityDao")
public class OrderAcceptAlacrityDao implements IOrderAcceptAlacrityDao {

	private SessionFactory sessionFactory;

	@Autowired
	public OrderAcceptAlacrityDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public OrderAcceptAlacrity get(Long id) {
		return (OrderAcceptAlacrity) sessionFactory.getCurrentSession().get(
				OrderAcceptAlacrity.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderAcceptAlacrity> getAll() {
		return sessionFactory.getCurrentSession()
				.createCriteria(OrderAcceptAlacrity.class).list();
	}

	@Override
	public void save(OrderAcceptAlacrity alacrity) {
		sessionFactory.getCurrentSession().save(alacrity);
	}

	@Override
	public void saveOrUpdate(OrderAcceptAlacrity alacrity) {
		sessionFactory.getCurrentSession().saveOrUpdate(alacrity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderAcceptAlacrity> getOrderAll(Long id) {
		return sessionFactory.getCurrentSession()
				.createCriteria(OrderAcceptAlacrity.class)
				.add(Restrictions.eq("orderId", id)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Broker getWinner(Order order) {

		List<OrderAcceptAlacrity> orderAlacrities = sessionFactory
				.getCurrentSession().createCriteria(OrderAcceptAlacrity.class)
				.add(Restrictions.eq("orderId", order.getId()))
				.addOrder(org.hibernate.criterion.Order.asc("order")).list();

		OrderAcceptAlacrity winnerAlacrity = orderAlacrities.get(0);

		return winnerAlacrity.getBroker();
	}
}
