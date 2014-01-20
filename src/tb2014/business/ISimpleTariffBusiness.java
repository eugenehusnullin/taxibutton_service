package tb2014.business;

import java.util.List;

import tb2014.domain.tariff.SimpleTariff;

public interface ISimpleTariffBusiness {

	SimpleTariff get(Long id);

	List<SimpleTariff> getAll();

	void save(SimpleTariff tariff);
}
