package tb2014.dao;

import java.util.List;

import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

public interface IOrderStatusDao {

	List<OrderStatus> get(Order order);

	OrderStatus getLast(Order order);

	void save(OrderStatus orderStatus);
}
