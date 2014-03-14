package tb2014.domain.order;

public enum OrderStatusType {

	Driving(1), Waiting(2), Transporting(3), Completed(4), Cancelled(5), Failed(6), Created(7), Taked(8), Prepared(9);

	@SuppressWarnings("unused")
	private int value;

	private OrderStatusType(int value) {
		this.value = value;
	}

	public static boolean IsValidForUserCancel(OrderStatusType statusType) {
		return statusType == OrderStatusType.Created || statusType == OrderStatusType.Taked
				|| statusType == OrderStatusType.Prepared || statusType == OrderStatusType.Driving;
	}

	public static boolean EndProcessingStatus(OrderStatusType statusType) {
		return statusType == OrderStatusType.Completed || statusType == OrderStatusType.Cancelled
				|| statusType == OrderStatusType.Failed;
	}
}
