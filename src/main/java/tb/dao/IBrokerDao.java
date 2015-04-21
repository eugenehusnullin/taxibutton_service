package tb.dao;

import java.util.List;

import tb.domain.Broker;

public interface IBrokerDao {

	Broker get(Long id);

	Broker get(String uuid);

	Broker getByApiId(String id);
	
	Broker getByApiId(String apiId, String apiKey);

	List<Broker> getAll();
	
	List<Broker> getActive();

	void save(Broker broker);
	
	void saveOrUpdate(Broker broker);
	
	void delete(Broker broker);

	List<Broker> getBrokersNeedMapareaSynch();
}
