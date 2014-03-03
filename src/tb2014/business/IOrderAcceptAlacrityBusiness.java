package tb2014.business;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;

public interface IOrderAcceptAlacrityBusiness {

	OrderAcceptAlacrity get(Long id);

	OrderAcceptAlacrity get(Order order, Broker broker);

	List<OrderAcceptAlacrity> getAll();

	List<OrderAcceptAlacrity> getAll(Order order);

	void save(OrderAcceptAlacrity alacrity);

	void saveOrUpdate(OrderAcceptAlacrity alacrity);

	Broker getWinner(Order order);
}
