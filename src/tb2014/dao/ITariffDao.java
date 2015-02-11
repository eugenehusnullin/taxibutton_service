package tb2014.dao;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.tariff.Tariff;

public interface ITariffDao {

	Tariff get(Long id);

	Tariff getActive(Broker broker, String tariffId);

	List<Tariff> getActive(Broker broker);

	void save(Tariff tariff);

	void saveOrUpdate(Tariff tariff);
	
	public List<Tariff> getAll();
}
