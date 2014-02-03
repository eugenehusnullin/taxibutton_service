package tb2014.business;

import java.util.List;

import tb2014.domain.order.Order;
import tb2014.domain.order.OrderCancel;

public interface IOrderCancelBusiness {

	OrderCancel get(Long id);

	OrderCancel get(Order order);

	List<OrderCancel> getAll();

	void save(OrderCancel orderCancel);

	void saveOrUpdate(OrderCancel orderCancel);
}
