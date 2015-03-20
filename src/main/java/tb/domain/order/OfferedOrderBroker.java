package tb.domain.order;

import java.io.Serializable;
import java.util.Date;

import tb.domain.Broker;

public class OfferedOrderBroker implements Serializable {
	private static final long serialVersionUID = 1679325554802755364L;
	private Order order;
	private Broker broker;
	private Date timestamp;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
