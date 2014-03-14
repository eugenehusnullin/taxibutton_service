package tb2014.service;

public class OrderNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9074099806576849287L;

	private String UUID;

	public OrderNotFoundException(String UUID) {
		this.setUUID(UUID);
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}
}
