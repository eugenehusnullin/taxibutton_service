package tb2014.service.exceptions;

public class BrokerNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8250483277974266894L;

	private String apiId;

	public BrokerNotFoundException(String apiId) {
		this.setApiId(apiId);
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
}
