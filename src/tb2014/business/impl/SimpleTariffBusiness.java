package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.ISimpleTariffBusiness;
import tb2014.dao.ISimpleTariffDao;
import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;

@Service("SimpleTariffBusiness")
public class SimpleTariffBusiness implements ISimpleTariffBusiness {

	private ISimpleTariffDao simpleTariffDao;

	@Autowired
	public SimpleTariffBusiness(ISimpleTariffDao simpleTariffDao) {
		this.simpleTariffDao = simpleTariffDao;
	}

	@Override
	public SimpleTariff get(Long id) {
		return simpleTariffDao.get(id);
	}

	@Override
	public List<SimpleTariff> getAll() {
		return simpleTariffDao.getAll();
	}

	@Override
	public void save(SimpleTariff tariff) {
		simpleTariffDao.save(tariff);
	}

	@Override
	public SimpleTariff get(Broker broker) {
		return simpleTariffDao.get(broker);
	}

	@Override
	public void saveOrUpdate(SimpleTariff tariff) {
		simpleTariffDao.saveOrUpdate(tariff);
	}

	@Override
	public List<SimpleTariff> getAllWithChilds() {
		return simpleTariffDao.getAllWithChilds();
	}
}
