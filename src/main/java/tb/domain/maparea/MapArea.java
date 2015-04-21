package tb.domain.maparea;

import java.util.Set;

import tb.domain.Broker;

public abstract class MapArea {

	private Long id;
	private String name;
	private String about;
	private Set<Broker> brokers;

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

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public Set<Broker> getBrokers() {
		return brokers;
	}

	public void setBrokers(Set<Broker> brokers) {
		this.brokers = brokers;
	}
}
