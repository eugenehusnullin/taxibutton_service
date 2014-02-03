package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IOrderCancelBusiness;
import tb2014.dao.IOrderCancelDao;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderCancel;

@Service("OrderCancelBusiness")
public class OrderCancelBusiness implements IOrderCancelBusiness {

	private IOrderCancelDao orderCancelDao;

	@Autowired
	public OrderCancelBusiness(IOrderCancelDao orderCancelDao) {
		this.orderCancelDao = orderCancelDao;
	}

	@Transactional(readOnly = true)
	@Override
	public OrderCancel get(Long id) {
		return orderCancelDao.get(id);
	}

	@Transactional(readOnly = true)
	@Override
	public OrderCancel get(Order order) {
		return orderCancelDao.get(order);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderCancel> getAll() {
		return orderCancelDao.getAll();
	}

	@Transactional
	@Override
	public void save(OrderCancel orderCancel) {
		orderCancelDao.save(orderCancel);
	}

	@Transactional
	@Override
	public void saveOrUpdate(OrderCancel orderCancel) {
		orderCancelDao.saveOrUpdate(orderCancel);
	}

}
