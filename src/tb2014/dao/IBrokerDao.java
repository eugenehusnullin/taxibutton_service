package tb2014.dao;

import java.util.List;

import tb2014.domain.Broker;

public interface IBrokerDao {
	Broker get(Long id);
	List<Broker> getAll();
	void save(Broker broker);
}
