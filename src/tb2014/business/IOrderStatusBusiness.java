package tb2014.business;

import java.util.List;

import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

public interface IOrderStatusBusiness {

	List<OrderStatus> get(Order order);

	OrderStatus getLast(Order order);

	void save(OrderStatus orderStatus);
}
