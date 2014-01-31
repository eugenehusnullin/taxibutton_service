package tb2014.dao;

import java.util.List;

import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

public interface IOrderStatusDao {

	OrderStatus get(Long id);

	OrderStatus get(Order order);

	List<OrderStatus> getAll();

	void save(OrderStatus orderStatus);
	
	void saveOrUpdate(OrderStatus orderStatus);
}
