package tb2014.domain.order;

import java.util.Date;

import tb2014.domain.Broker;

public class OrderAcceptAlacrity {
	
	private Long id;
	private Broker broker;
	private Order order;
	private Date date;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
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
}
