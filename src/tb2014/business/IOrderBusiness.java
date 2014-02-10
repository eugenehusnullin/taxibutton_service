package tb2014.business;

import java.util.List;

import tb2014.domain.Device;
import tb2014.domain.order.Order;

public interface IOrderBusiness {

	Order get(Long id);

	Order get(String uuid);

	Order getWithChilds(Long id);

	Order getWithChilds(String uuid);
	
	List<Order> getAll();

	List<Order> getAll(Device device);

	void save(Order order);

	void delete(Order order);

	void saveOrUpdate(Order order);
}
