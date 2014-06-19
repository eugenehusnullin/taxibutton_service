package tb2014.dao;

import java.util.List;

import tb2014.domain.Broker;

public interface IBrokerDao {

	Broker get(Long id);

	Broker get(String uuid);

	Broker getByApiId(String id);
	
	Broker getByApiId(String apiId, String apiKey);

	List<Broker> getAll();

	void save(Broker broker);
	
	void saveOrUpdate(Broker broker);
	
	void delete(Broker broker);
}
