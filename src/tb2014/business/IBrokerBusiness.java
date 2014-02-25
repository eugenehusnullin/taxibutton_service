package tb2014.business;

import java.util.List;

import tb2014.domain.Broker;

public interface IBrokerBusiness {
	
	Broker get(String uuid);
	
	Broker getById(Long id);

	Broker getByApiId(String id);
	
	List<Broker> getAll();

	void add(Broker broker);
}
