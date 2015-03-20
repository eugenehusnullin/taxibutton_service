package tb.service;

import java.util.Date;

import tb.domain.order.Order;

public class OrderExecHolder {
	private Order order;
	private int attemptCount;
	private Date startOffer;
	private Date startChooseWinner;

	public OrderExecHolder(Order order, Date startOffer) {
		this(order, 1, startOffer);
	}

	public OrderExecHolder(Order order, int attemptCount, Date startOffer) {
		this.order = order;
		this.attemptCount = attemptCount;
		this.startOffer = startOffer;
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

	public Date getStartOffer() {
		return startOffer;
	}

	public Date getStartChooseWinner() {
		return startChooseWinner;
	}

	public void setStartChooseWinner(Date startChooseWinner) {
		this.startChooseWinner = startChooseWinner;
	}
}
