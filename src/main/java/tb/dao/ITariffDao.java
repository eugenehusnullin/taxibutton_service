package tb.dao;

import java.util.List;

import tb.domain.Broker;
import tb.domain.Tariff;

public interface ITariffDao {
	List<Tariff> get(Broker broker);

	void saveOrUpdate(Tariff tariff);

	public List<Tariff> getAll();
}
