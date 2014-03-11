package tb2014.business;

import java.util.List;

import tb2014.domain.order.Order;

public interface IOrderBusiness {

	Order get(Long id);

	Order getByUuid(String uuid);

	List<Order> getPagination(String orderField, String orderDirection, int start, int count);

	void saveNewOrder(Order order);

	void delete(Order order);

	void saveOrUpdate(Order order);

	Long getAllOrdersCount();
}
