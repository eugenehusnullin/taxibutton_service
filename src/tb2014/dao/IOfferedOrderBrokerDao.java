package tb2014.dao;

import java.util.List;

import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;

public interface IOfferedOrderBrokerDao {
	void save(OfferedOrderBroker offeredOrderBroker);
	List<OfferedOrderBroker> get(Order order);
}
