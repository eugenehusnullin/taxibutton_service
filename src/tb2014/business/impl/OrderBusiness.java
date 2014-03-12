package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IOrderBusiness;
import tb2014.dao.IOrderDao;
import tb2014.domain.order.Order;

@Service("OrderBusiness")
public class OrderBusiness implements IOrderBusiness {

	private IOrderDao orderDao;

	@Autowired
	public OrderBusiness(IOrderDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public Order get(Long id) {
		return orderDao.get(id);
	}

	@Override
	public void saveNewOrder(Order order) {
		orderDao.save(order);
	}

	@Override
	public void saveOrUpdate(Order order) {
		orderDao.saveOrUpdate(order);
	}

	@Override
	public void delete(Order order) {
		orderDao.delete(order);
	}

	@Override
	public Order getByUuid(String uuid) {
		return orderDao.get(uuid);
	}

	@Override
	public List<Order> getPagination(String orderField, String orderDirection, int start, int count) {
		return orderDao.getPagination(orderField, orderDirection, start, count);
	}

	@Override
	public Long getAllOrdersCount() {
		return orderDao.getAllOrdersCount();
	}
}
