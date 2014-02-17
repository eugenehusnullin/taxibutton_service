package tb2014.dao;

import java.util.List;

import tb2014.domain.Device;
import tb2014.domain.order.AddressPoint;
import tb2014.domain.order.Order;

public interface IOrderDao {

	Order get(Long id);

	Order get(String uuid);

	Order getWithChilds(Long id);

	Order getWithChilds(String uuid);

	List<Order> getAll();

	List<Order> getAllWithChilds();

	List<Order> getAll(Device device);
	
	List<Order> getAllWithParams(String orderField, String orderDirection, int start, int count);

	void save(Order order);

	void delete(Order order);

	void saveOrUpdate(Order order);

	AddressPoint getSourcePoint(Order order);
	
	Long getAllOrdersCount();
}
