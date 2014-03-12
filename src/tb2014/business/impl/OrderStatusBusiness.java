package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public List<OrderStatus> get(Order order) {
		return orderStatusDao.get(order);
	}

	@Override
	public void save(OrderStatus orderStatus) {
		orderStatusDao.save(orderStatus);
	}

	@Override
	public OrderStatus getLast(Order order) {
		return orderStatusDao.getLast(order);
	}
}
