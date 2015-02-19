package tb.dao;

import java.util.List;

import tb.domain.Broker;
import tb.domain.order.Order;
import tb.domain.order.OrderAcceptAlacrity;

public interface IOrderAcceptAlacrityDao {

	OrderAcceptAlacrity get(Order order, Broker broker);

	List<OrderAcceptAlacrity> getAll(Order order);

	void save(OrderAcceptAlacrity alacrity);

	Broker getWinner(Order order);
}
