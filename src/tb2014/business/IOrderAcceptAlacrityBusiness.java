package tb2014.business;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;

public interface IOrderAcceptAlacrityBusiness {

	OrderAcceptAlacrity get(Long id);

	List<OrderAcceptAlacrity> getAll();
	
	List<OrderAcceptAlacrity> getOrderAll(Long id);

	void save(OrderAcceptAlacrity alacrity);

	void saveOrUpdate(OrderAcceptAlacrity alacrity);
	
	Broker getWinner(Order order);
}
