package tb2014.business;

import java.util.List;

import tb2014.domain.order.Order;

public interface IOrderBusiness {

	Order get(Long id);

	Order getWithChilds(Long id);
	
	List<Order> getAll();

	void save(Order order);

	void saveOrUpdate(Order order);
}
