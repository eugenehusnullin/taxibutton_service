package tb.service.exceptions;

public class DeviceNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1385336162231011850L;

	private String deviceApiId;

	public DeviceNotFoundException(String deviceApiId) {
		this.setDeviceApiId(deviceApiId);
	}

	public String getDeviceApiId() {
		return deviceApiId;
	}

	public void setDeviceApiId(String deviceApiId) {
		this.deviceApiId = deviceApiId;
	}

}
