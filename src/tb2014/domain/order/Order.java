package tb2014.domain.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import tb2014.domain.Broker;
import tb2014.domain.order.AddressPoint;

public class Order {

	private Long id;
	private String type;
	private String phone;
	private Date supplyDate;
	private int supplyHour;
	private int supplyMin;
	private Broker broker;
	private List<AddressPoint> destinations;
	private Set<Requirement> requirements;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AddressPoint getSource() {

		for (AddressPoint currentPoint : destinations) {

			if (currentPoint.getType() == 0) {
				return currentPoint;
			}
		}

		return null;
	}

	public void setSource(AddressPoint source) {

		for (AddressPoint currentPoint : destinations) {

			if (currentPoint.getIndexNumber() == 1) {
				currentPoint = source;
			}
		}
	}

	public Set<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(Set<Requirement> requirements) {
		this.requirements = requirements;
	}
}
