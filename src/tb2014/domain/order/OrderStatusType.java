package tb2014.domain.order;

public enum OrderStatusType {

	Driving(1), Waiting(2), Transporting(3), Completed(4), Cancelled(5), Failed(
			6);

	private int value;

	private OrderStatusType(int value) {
		this.value = value;
	}
}
