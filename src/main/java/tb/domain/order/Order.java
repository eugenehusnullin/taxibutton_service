package tb.domain.order;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import tb.domain.Broker;
import tb.domain.Device;

public class Order {

	private Long id;
	private String uuid;
	private Device device;
	private String phone;
	private Date bookingDate;
	private Broker broker;
	private String carUuid;
	private SortedSet<AddressPoint> destinations;
	private Set<Requirement> requirements;
	private Set<OrderStatus> statuses;
	private Set<OrderCancel> orderCancel;
	private Set<Broker> offerBrokerList;
	private VehicleClass orderVehicleClass;
	private Boolean notlater;
	private String comments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public SortedSet<AddressPoint> getDestinations() {
		return destinations;
	}

	public void setDestinations(SortedSet<AddressPoint> destinations) {
		this.destinations = destinations;
	}

	public AddressPoint getSource() {
		for (AddressPoint currentPoint : destinations) {
			if (currentPoint.getIndexNumber() == 0) {
				return currentPoint;
			}
		}
		return null;
	}

	public OrderStatus getLastStatus() {
		return statuses.iterator().next();
	}

	public Set<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(Set<Requirement> requirements) {
		this.requirements = requirements;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String guid) {
		this.uuid = guid;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Set<OrderStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(Set<OrderStatus> statuses) {
		this.statuses = statuses;
	}

	public Set<OrderCancel> getOrderCancel() {
		return orderCancel;
	}

	public void setOrderCancel(Set<OrderCancel> orderCancel) {
		this.orderCancel = orderCancel;
	}

	public VehicleClass getOrderVehicleClass() {
		return orderVehicleClass;
	}

	public void setOrderVehicleClass(VehicleClass orderVehicleClass) {
		this.orderVehicleClass = orderVehicleClass;
	}

	public Set<Broker> getOfferBrokerList() {
		return offerBrokerList;
	}

	public void setOfferBrokerList(Set<Broker> offerBrokerList) {
		this.offerBrokerList = offerBrokerList;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Boolean getNotlater() {
		return notlater;
	}

	public void setNotlater(Boolean notlater) {
		this.notlater = notlater;
	}

	public String getCarUuid() {
		return carUuid;
	}

	public void setCarUuid(String carUuid) {
		this.carUuid = carUuid;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
