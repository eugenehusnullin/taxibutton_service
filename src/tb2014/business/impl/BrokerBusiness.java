package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IBrokerBusiness;
import tb2014.dao.IBrokerDao;
import tb2014.domain.Broker;

@Service("BrokerBusiness")
public class BrokerBusiness implements IBrokerBusiness {

	private IBrokerDao brokerDao;

	@Autowired
	public BrokerBusiness(IBrokerDao brokerDao) {
		this.brokerDao = brokerDao;
	}

	@Override
	public Broker getById(Long id) {
		return brokerDao.get(id);
	}

	@Override
	public List<Broker> getAll() {
		return brokerDao.getAll();
	}

	@Override
	public void add(Broker broker) {
		brokerDao.save(broker);
	}

	@Override
	public Broker getByApiId(String id) {
		return brokerDao.getByApiId(id);
	}

	@Override
	public Broker get(String uuid) {
		return brokerDao.get(uuid);
	}

	@Override
	public void delete(Broker broker) {
		brokerDao.delete(broker);
	}

	@Override
	public void saveOrUpdate(Broker broker) {
		brokerDao.saveOrUpdate(broker);
	}

}
