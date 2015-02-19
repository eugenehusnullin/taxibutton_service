package tb.dao;

import java.util.List;

import tb.domain.order.Order;
import tb.domain.order.OrderStatus;

public interface IOrderStatusDao {

	List<OrderStatus> get(Order order);

	OrderStatus getLast(Order order);

	void save(OrderStatus orderStatus);
}
