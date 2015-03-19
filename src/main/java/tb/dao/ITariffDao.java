package tb.dao;

import java.util.List;

import tb.domain.Broker;
import tb.domain.Tariff;

public interface ITariffDao {

	Tariff getActive(Broker broker, String tariffId);

	List<Tariff> getActive(Broker broker);

	void saveOrUpdate(Tariff tariff);
	
	public List<Tariff> getAll();
}
