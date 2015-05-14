package tb.domain.maparea;

import java.util.Set;

import tb.domain.Broker;
import tb.domain.TariffDefinition;

public abstract class MapArea {

	private Long id;
	private String name;
	private String about;
	private Set<Broker> brokers;
	private Set<TariffDefinition> tariffDefinitions;

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

	public Set<TariffDefinition> getTariffDefinitions() {
		return tariffDefinitions;
	}

	public void setTariffDefinitions(Set<TariffDefinition> tariffDefinitions) {
		this.tariffDefinitions = tariffDefinitions;
	}
}
