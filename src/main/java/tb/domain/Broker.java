package tb.domain;

import java.util.Set;

import tb.domain.maparea.MapArea;

public class Broker {

	private Long id;
	private String uuid;
	private String apiId;
	private String apiKey;
	private String name;
	private String apiurl;
	private String tariffUrl;
	private String driverUrl;
	private String mapareaUrl;
	private Set<MapArea> mapAreas;
	private SmsMethod smsMethod;
	private TariffType tariffType;
	private Integer timezoneOffset;

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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Set<MapArea> getMapAreas() {
		return mapAreas;
	}

	public void setMapAreas(Set<MapArea> mapAreas) {
		this.mapAreas = mapAreas;
	}

	public SmsMethod getSmsMethod() {
		return smsMethod;
	}

	public void setSmsMethod(SmsMethod smsMethod) {
		this.smsMethod = smsMethod;
	}

	public String getTariffUrl() {
		return tariffUrl;
	}

	public void setTariffUrl(String tariffUrl) {
		this.tariffUrl = tariffUrl;
	}

	public String getDriverUrl() {
		return driverUrl;
	}

	public void setDriverUrl(String driverUrl) {
		this.driverUrl = driverUrl;
	}

	public TariffType getTariffType() {
		return tariffType;
	}

	public void setTariffType(TariffType tariffType) {
		this.tariffType = tariffType;
	}

	public Integer getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(Integer timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public String getMapareaUrl() {
		return mapareaUrl;
	}

	public void setMapareaUrl(String mapareaUrl) {
		this.mapareaUrl = mapareaUrl;
	}
}
