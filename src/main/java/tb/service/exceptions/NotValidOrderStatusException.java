package tb.service.exceptions;

import tb.domain.order.OrderStatus;

public class NotValidOrderStatusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8278212086101612356L;

	private OrderStatus status;

	public NotValidOrderStatusException(OrderStatus status) {
		this.status = status;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
}
