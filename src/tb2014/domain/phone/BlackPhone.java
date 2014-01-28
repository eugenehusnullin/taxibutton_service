package tb2014.domain.phone;

import tb2014.domain.Broker;

public class BlackPhone {

	private int id;
	private Broker broker;
	private String number;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
