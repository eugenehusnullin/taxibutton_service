package tb2014.domain.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import tb2014.domain.Broker;
import tb2014.domain.Device;

public class Order {

	private Long id;
	private String uuid;
	private Device device;
	private Boolean urgent;
	private String phone;
	private Date supplyDate;
	private int supplyHour;
	private int supplyMin;
	private Broker broker;
	private List<AddressPoint> destinations;
	private Set<Requirement> requirements;
	private Set<OrderStatus> statuses;
	private Set<OrderCancel> orderCancel;
	private Set<GeoData> geoData;
	private Set<Broker> offerBrokerList;
	private VehicleClass orderVehicleClass;
	
	// don't persistent fields
	private Date startOffer;

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

	public Date getSupplyDate() {
		return supplyDate;
	}

	public void setSupplyDate(Date supplyDate) {
		this.supplyDate = supplyDate;
	}

	public int getSupplyHour() {
		return supplyHour;
	}

	public void setSupplyHour(int supplyHour) {
		this.supplyHour = supplyHour;
	}

	public int getSupplyMin() {
		return supplyMin;
	}

	public void setSupplyMin(int supplyMin) {
		this.supplyMin = supplyMin;
	}

	public Date getSupplyTime() {

		Date result = new Date();
		result.setTime(this.supplyDate.getTime() + this.supplyHour * 3600000 + this.supplyMin * 60000);

		return result;
	}

	public String getSupplyTime(String format) {

		DateFormat df = new SimpleDateFormat(format);
		String result = df.format(this.getSupplyTime());

		return result;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public List<AddressPoint> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<AddressPoint> destinations) {
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

	public Set<GeoData> getGeoData() {
		return geoData;
	}

	public void setGeoData(Set<GeoData> geoData) {
		this.geoData = geoData;
	}

	public Boolean getUrgent() {
		return urgent;
	}

	public void setUrgent(Boolean urgent) {
		this.urgent = urgent;
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

	public Date getStartOffer() {
		return startOffer;
	}

	public void setStartOffer(Date startOffer) {
		this.startOffer = startOffer;
	}
}
