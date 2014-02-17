package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IOrderBusiness;
import tb2014.dao.IOrderDao;
import tb2014.domain.Device;
import tb2014.domain.order.AddressPoint;
import tb2014.domain.order.Order;

@Service("OrderBusiness")
public class OrderBusiness implements IOrderBusiness {

	private IOrderDao orderDao;

	@Autowired
	public OrderBusiness(IOrderDao orderDao) {
		this.orderDao = orderDao;
	}

	@Transactional(readOnly = true)
	@Override
	public Order get(Long id) {
		return orderDao.get(id);
	}

	@Transactional(readOnly = true)
	@Override
	public Order getWithChilds(Long id) {
		return orderDao.getWithChilds(id);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Order> getAll() {
		return orderDao.getAll();
	}

	@Transactional
	@Override
	public void save(Order order) {
		orderDao.save(order);
	}

	@Transactional
	@Override
	public void saveOrUpdate(Order order) {
		orderDao.saveOrUpdate(order);
	}

	@Transactional
	@Override
	public void delete(Order order) {
		orderDao.delete(order);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Order> getAll(Device device) {
		return orderDao.getAll(device);
	}

	@Transactional(readOnly = true)
	@Override
	public Order get(String uuid) {
		return orderDao.get(uuid);
	}

	@Transactional(readOnly = true)
	@Override
	public Order getWithChilds(String uuid) {
		return orderDao.getWithChilds(uuid);
	}

	@Transactional(readOnly = true)
	@Override
	public AddressPoint getSourcePoint(Order order) {
		return orderDao.getSourcePoint(order);
	}

	@Transactional
	@Override
	public List<Order> getAllWithChilds() {
		return orderDao.getAllWithChilds();
	}

	@Transactional(readOnly = true)
	@Override
	public List<Order> getAllWithParams(String orderField,
			String orderDirection, int start, int count) {
		return orderDao.getAllWithParams(orderField, orderDirection, start,
				count);
	}

	@Transactional(readOnly = true)
	@Override
	public Long getAllOrdersCount() {
		return orderDao.getAllOrdersCount();
	}
}
