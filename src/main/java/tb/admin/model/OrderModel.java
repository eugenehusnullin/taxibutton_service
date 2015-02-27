package tb.admin.model;

import java.util.Date;

public class OrderModel {
	private Long id;
	private Date bookingDate;
	private String sourceShortAddress;
	private Boolean urgent;
	private String phone;
	private String lastStatus;
	private String uuid;
	private String brokerName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getSourceShortAddress() {
		return sourceShortAddress;
	}

	public void setSourceShortAddress(String sourceShortAddress) {
		this.sourceShortAddress = sourceShortAddress;
	}

	public Boolean getUrgent() {
		return urgent;
	}

	public void setUrgent(Boolean urgent) {
		this.urgent = urgent;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
}