package tb2014.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IOrderCancelBusiness;
import tb2014.dao.IOrderCancelDao;
import tb2014.domain.order.OrderCancel;

@Service("OrderCancelBusiness")
public class OrderCancelBusiness implements IOrderCancelBusiness {

	private IOrderCancelDao orderCancelDao;

	@Autowired
	public OrderCancelBusiness(IOrderCancelDao orderCancelDao) {
		this.orderCancelDao = orderCancelDao;
	}

	@Override
	public void save(OrderCancel orderCancel) {
		orderCancelDao.save(orderCancel);
	}

}
