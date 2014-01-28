package tb2014.dao;

import java.util.List;

import tb2014.domain.order.Order;

public interface IOrderDao {

	Order get(Long id);

	Order getWithChilds(Long id);
	
	List<Order> getAll();

	void save(Order order);

	void saveOrUpdate(Order order);
}
