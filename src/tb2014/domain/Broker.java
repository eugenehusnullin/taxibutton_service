package tb2014.domain;

import java.util.Set;

import tb2014.domain.order.Order;

public class Broker {

	private Long id;
	private String uuid;
	private String apiId;
	private String apiKey;
	private String name;
	private String apiurl;
	private Set<Order> offerOrderList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApiurl() {
		return apiurl;
	}

	public void setApiurl(String apiurl) {
		this.apiurl = apiurl;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Set<Order> getOfferOrderList() {
		return offerOrderList;
	}

	public void setOfferOrderList(Set<Order> offerOrderList) {
		this.offerOrderList = offerOrderList;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
