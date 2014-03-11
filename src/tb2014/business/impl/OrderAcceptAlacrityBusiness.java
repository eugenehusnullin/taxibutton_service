package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.dao.IOrderAcceptAlacrityDao;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;

@Service("OrderAcceptAlacrityBusiness")
public class OrderAcceptAlacrityBusiness implements IOrderAcceptAlacrityBusiness {

	private IOrderAcceptAlacrityDao alacrityDao;

	@Autowired
	public OrderAcceptAlacrityBusiness(IOrderAcceptAlacrityDao alacrityDao) {
		this.alacrityDao = alacrityDao;
	}

	@Transactional(readOnly = true)
	@Override
	public OrderAcceptAlacrity get(Order order, Broker broker) {
		return alacrityDao.get(order, broker);
	}

	@Transactional
	@Override
	public void save(OrderAcceptAlacrity alacrity) {
		alacrityDao.save(alacrity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderAcceptAlacrity> getAll(Order order) {
		return alacrityDao.getAll(order);
	}

	@Transactional(readOnly = true)
	@Override
	public Broker getWinner(Order order) {
		return alacrityDao.getWinner(order);
	}
}
