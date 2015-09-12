package tb.service;

import java.util.Date;

import tb.domain.order.Order;

public class OrderExecHolder {
	private Order order;
	private int attemptCount;
	private Date startChooseWinner;

	public OrderExecHolder(Order order) {
		this(order, 1);
	}

	public OrderExecHolder(Order order, int attemptCount) {
		this.order = order;
		this.attemptCount = attemptCount;
	}
	
	public int incrementAttempt() {
		this.attemptCount++;
		return this.attemptCount;
	}

	public Order getOrder() {
		return order;
	}

	public int getAttemptCount() {
		return attemptCount;
	}

	public Date getStartChooseWinner() {
		return startChooseWinner;
	}

	public void setStartChooseWinner(Date startChooseWinner) {
		this.startChooseWinner = startChooseWinner;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OrderExecHolder)) {
			return super.equals(obj);
		}
		
		OrderExecHolder other = (OrderExecHolder) obj;
		return this.order.getId().equals(other.order.getId());
	}
}
