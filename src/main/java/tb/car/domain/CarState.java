package tb.car.domain;

import java.io.Serializable;
import java.util.Date;

public class CarState implements Serializable {
	private static final long serialVersionUID = 1586844821594975295L;

	private Long brokerId;
	private String uuid;

	private CarStateEnum state;
	private Date date;
	private double latitude;
	private double longitude;

	public CarStateEnum getState() {
		return state;
	}

	public void setState(CarStateEnum state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Long getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Long brokerId) {
		this.brokerId = brokerId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
