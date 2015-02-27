package tb.domain.order;

public class AddressPoint implements Comparable<AddressPoint> {

	private Long id;
	private Order order;
	private double lon;
	private double lat;
	private int indexNumber;
	private String fullAddress;
	private String shortAddress;
	private String closesStation;
	private String county;
	private String locality;
	private String street;
	private String housing;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getShortAddress() {
		return shortAddress;
	}

	public void setShortAddress(String shortAddress) {
		this.shortAddress = shortAddress;
	}

	public String getClosesStation() {
		return closesStation;
	}

	public void setClosesStation(String closesStation) {
		this.closesStation = closesStation;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHousing() {
		return housing;
	}

	public void setHousing(String housing) {
		this.housing = housing;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getIndexNumber() {
		return indexNumber;
	}

	public void setIndexNumber(int indexNumber) {
		this.indexNumber = indexNumber;
	}

	@Override
	public int compareTo(AddressPoint ap) {
		if (indexNumber < ap.indexNumber) {
			return -1;
		} else if (indexNumber == ap.indexNumber) {
			return 0;
		} else {
			return 1;
		}
	}
}
