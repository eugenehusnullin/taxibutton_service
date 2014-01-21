package tb2014.domain.tariff;

import tb2014.domain.Broker;

public class SimpleTariff {

	private int id;
	private Broker broker;
	private String tariffs;
	
	public Broker getBroker() {
		return broker;
	}
	
	public void setBroker(Broker broker) {
		this.broker = broker;
	}
	
	public String getTariffs() {
		return tariffs;
	}
	
	public void setTariffs(String tariffs) {
		this.tariffs = tariffs;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
