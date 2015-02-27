package tb.dao;

import java.util.List;

import tb.domain.order.OfferedOrderBroker;
import tb.domain.order.Order;

public interface IOfferedOrderBrokerDao {
	void save(OfferedOrderBroker offeredOrderBroker);
	List<OfferedOrderBroker> get(Order order);
	Long count(Order order);
}
