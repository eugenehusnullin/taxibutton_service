package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(readOnly=true)
	@Override
	public Broker getById(Long id) {
		return brokerDao.get(id);
	}

	@Transactional(readOnly=true)
	@Override
	public List<Broker> getAll() {
		return brokerDao.getAll();
	}

	@Transactional
	@Override
	public void add(Broker broker) {
		brokerDao.save(broker);
	}

	@Transactional(readOnly=true)
	@Override
	public Broker getByApiId(String id) {
		return brokerDao.getByApiId(id);
	}

	@Transactional(readOnly=true)
	@Override
	public Broker get(String uuid) {
		return brokerDao.get(uuid);
	}

}
