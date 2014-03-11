package tb2014.dao;

import java.util.List;

import tb2014.domain.order.Order;

public interface IOrderDao {

	Order get(Long id);

	Order get(String uuid);

	List<Order> getPagination(String orderField, String orderDirection, int start, int count);

	void save(Order order);

	void delete(Order order);

	void saveOrUpdate(Order order);

	Long getAllOrdersCount();
}
