package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IOrderAcceptAlacrityDao;
import tb.domain.Broker;
import tb.domain.order.Order;
import tb.domain.order.OrderAcceptAlacrity;

@Repository("OrderAcceptAlacrityDao")
public class OrderAcceptAlacrityDao implements IOrderAcceptAlacrityDao {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public OrderAcceptAlacrity get(Order order, Broker broker, String uuid) {
		return (OrderAcceptAlacrity) sessionFactory.getCurrentSession().createCriteria(OrderAcceptAlacrity.class)
				.add(Restrictions.eq("order", order))
				.add(Restrictions.eq("broker", broker))
				.add(Restrictions.eq("uuid", uuid))
				.uniqueResult();
	}

	@Override
	public void save(OrderAcceptAlacrity alacrity) {
		sessionFactory.getCurrentSession().save(alacrity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderAcceptAlacrity> getAll(Order order) {
		return sessionFactory.getCurrentSession().createCriteria(OrderAcceptAlacrity.class)
				.add(Restrictions.eq("order", order)).list();
	}

	@Override
	public OrderAcceptAlacrity getWinner(Order order) {

		OrderAcceptAlacrity winnerAlacrity = (OrderAcceptAlacrity) sessionFactory.getCurrentSession()
				.createCriteria(OrderAcceptAlacrity.class)
				.add(Restrictions.eq("order", order))
				.add(Restrictions.eqOrIsNull("fail", false))
				.addOrder(org.hibernate.criterion.Order.asc("date"))
				.setMaxResults(1)
				.uniqueResult();

		return winnerAlacrity;
	}
}
