package tb2014.business;

import java.util.List;

import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;

public interface IOfferedOrderBrokerBusiness {
	void save(OfferedOrderBroker offeredOrderBroker);
	List<OfferedOrderBroker> get(Order order);
	Long count(Order order);
}
