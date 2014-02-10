package tb2014.business;

import java.util.List;

import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

public interface IOrderStatusBusiness {

	OrderStatus get(Long id);

	List<OrderStatus> get(Order order);

	OrderStatus getLast(Order order);
	
	OrderStatus getLastWithChilds(Order order);
	
	List<OrderStatus> getAll();

	void save(OrderStatus orderStatus);

	void saveOrUpdate(OrderStatus orderStatus);
}
