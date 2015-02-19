package tb.dao;

import java.util.List;

import tb.domain.Broker;
import tb.domain.Tariff;

public interface ITariffDao {

	Tariff get(Long id);

	Tariff getActive(Broker broker, String tariffId);

	List<Tariff> getActive(Broker broker);

	void save(Tariff tariff);

	void saveOrUpdate(Tariff tariff);
	
	public List<Tariff> getAll();
}
