package tb.service.exceptions;

public class OrderNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9074099806576849287L;

	private String uuid;

	public OrderNotFoundException(String uuid) {
		this.setUuid(uuid);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
