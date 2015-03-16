package tb.domain.order;

import java.io.Serializable;
import java.util.Date;

import tb.domain.Broker;

public class OrderAcceptAlacrity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Broker broker;
	private Order order;
	private String uuid;
	private Date date;	

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
