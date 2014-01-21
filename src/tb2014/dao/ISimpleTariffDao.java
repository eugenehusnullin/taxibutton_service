package tb2014.dao;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;

public interface ISimpleTariffDao {

	SimpleTariff get(Long id);
	
	SimpleTariff get(Broker broker);

	List<SimpleTariff> getAll();

	void save(SimpleTariff tariff);
}
