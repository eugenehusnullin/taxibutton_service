package tb2014.dao;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;

public interface IOrderAcceptAlacrityDao {

	OrderAcceptAlacrity get(Order order, Broker broker);

	List<OrderAcceptAlacrity> getAll(Order order);

	void save(OrderAcceptAlacrity alacrity);

	Broker getWinner(Order order);
}
