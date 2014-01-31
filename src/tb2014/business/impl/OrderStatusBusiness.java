package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IOrderStatusBusiness;
import tb2014.dao.IOrderStatusDao;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

@Service("OrderStatusBusiness")
public class OrderStatusBusiness implements IOrderStatusBusiness {

	private IOrderStatusDao orderStatusDao;

	@Autowired
	public OrderStatusBusiness(IOrderStatusDao orderStatusDao) {
		this.orderStatusDao = orderStatusDao;
	}

	@Transactional(readOnly = true)
	@Override
	public OrderStatus get(Long id) {
		return orderStatusDao.get(id);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderStatus> get(Order order) {
		return orderStatusDao.get(order);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderStatus> getAll() {
		return orderStatusDao.getAll();
	}

	@Transactional
	@Override
	public void save(OrderStatus orderStatus) {
		orderStatusDao.save(orderStatus);
	}

	@Transactional
	@Override
	public void saveOrUpdate(OrderStatus orderStatus) {
		orderStatusDao.saveOrUpdate(orderStatus);
	}
}
