package tb2014.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.dao.IBrokerDao;
import tb2014.domain.Broker;

@Service
public class BrokerService {

	@Autowired
	private IBrokerDao brokerDao;

	@Transactional
	public List<Broker> getAll() {
		return brokerDao.getAll();
	}

	@Transactional
	public void add(Broker broker) {
		broker.setUuid(UUID.randomUUID().toString());
		brokerDao.save(broker);
	}

	@Transactional
	public void delete(Long brokerId) {
		Broker broker = brokerDao.get(brokerId);
		brokerDao.delete(broker);
	}

	@Transactional
	public Broker get(Long brokerId) {
		return brokerDao.get(brokerId);
	}

	@Transactional
	public void update(Long brokerId, String apiId, String apiKey, String name, String apiUrl) {
		Broker broker = brokerDao.get(brokerId);

		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setApiurl(apiUrl);
		broker.setName(name);

		brokerDao.saveOrUpdate(broker);
	}

}
