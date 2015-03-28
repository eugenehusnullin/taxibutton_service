package tb.car.domain;

import java.io.Serializable;
import java.util.Date;

public class GeoData implements Serializable {
	private static final long serialVersionUID = 7979890724903199836L;
	private Long id;
	
	private Long brokerId;
	private String uuid;

	private Double lat;
	private Double lon;
	private int direction;
	private Double speed;
	private String category;
	private Date date;

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
