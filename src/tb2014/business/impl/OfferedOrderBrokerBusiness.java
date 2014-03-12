package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IOfferedOrderBrokerBusiness;
import tb2014.dao.IOfferedOrderBrokerDao;
import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;

@Service("OfferedOrderBrokerBusiness")
public class OfferedOrderBrokerBusiness implements IOfferedOrderBrokerBusiness {

	private IOfferedOrderBrokerDao offeredOrderBrokerDao;

	@Autowired
	public OfferedOrderBrokerBusiness(IOfferedOrderBrokerDao offeredOrderBrokerDao) {
		this.offeredOrderBrokerDao = offeredOrderBrokerDao;
	}

	@Override
	public void save(OfferedOrderBroker offeredOrderBroker) {
		offeredOrderBrokerDao.save(offeredOrderBroker);
	}

	@Override
	public List<OfferedOrderBroker> get(Order order) {
		return offeredOrderBrokerDao.get(order);
	}

	@Override
	public Long count(Order order) {
		return offeredOrderBrokerDao.count(order);
	}

}
