package tb2014.domain.tariff;

public class SimpleTariff {

	private Long brokerId;
	private String tariffs;
	
	public Long getBrokerId() {
		return brokerId;
	}
	
	public void setBrokerId(Long brokerId) {
		this.brokerId = brokerId;
	}
	
	public String getTariffs() {
		return tariffs;
	}
	
	public void setTariffs(String tariffs) {
		this.tariffs = tariffs;
	}
}
