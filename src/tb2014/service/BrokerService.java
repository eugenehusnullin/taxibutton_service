package tb2014.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IBrokerBusiness;
import tb2014.domain.Broker;

@Service
public class BrokerService {

	@Autowired
	private IBrokerBusiness brokerBusiness;

	@Transactional
	public List<Broker> getAll() {
		return brokerBusiness.getAll();
	}

	@Transactional
	public void add(Broker broker) {
		broker.setUuid(UUID.randomUUID().toString());
		brokerBusiness.add(broker);
	}

	@Transactional
	public void delete(Long brokerId) {
		Broker broker = brokerBusiness.getById(brokerId);
		brokerBusiness.delete(broker);
	}

	@Transactional
	public Broker get(Long brokerId) {
		return brokerBusiness.getById(brokerId);
	}

	@Transactional
	public void update(Long brokerId, String apiId, String apiKey, String name, String apiUrl) {
		Broker broker = brokerBusiness.getById(brokerId);

		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setApiurl(apiUrl);
		broker.setName(name);

		brokerBusiness.saveOrUpdate(broker);
	}

}
