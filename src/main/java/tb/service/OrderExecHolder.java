package tb.service;

import tb.domain.order.Order;

public class OrderExecHolder {
	private Order order;
	private int attemptCount;

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
}
