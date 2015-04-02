package tb.domain.order;

public enum OrderStatusType {

	Driving(1), Waiting(2), Transporting(3), Completed(4), Cancelled(5), Failed(6), Created(7), Taked(8), Prepared(9), Notified(10);

	@SuppressWarnings("unused")
	private int value;

	private OrderStatusType(int value) {
		this.value = value;
	}

	public static boolean EndProcessingStatus(OrderStatusType statusType) {
		return statusType == OrderStatusType.Completed || statusType == OrderStatusType.Cancelled
				|| statusType == OrderStatusType.Failed;
	}

	public static boolean IsValidForOffer(OrderStatusType status) {
		return status == OrderStatusType.Created;
	}
	
	public static boolean IsValidForTake(OrderStatusType status) {
		return status == OrderStatusType.Created;
	}
	
	public static boolean IsValidForUserGeodata(OrderStatusType statusType) {
		return statusType == OrderStatusType.Driving || statusType == OrderStatusType.Waiting
				|| statusType == OrderStatusType.Transporting;
	}
}
