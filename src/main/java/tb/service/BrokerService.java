package tb.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.IBrokerDao;
import tb.dao.IMapAreaDao;
import tb.domain.Broker;
import tb.domain.SmsMethod;
import tb.domain.TariffType;
import tb.domain.maparea.MapArea;

@Service
public class BrokerService {

	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private IMapAreaDao mapAreaDao;

	@Transactional
	public void addMapArea(MapArea mapArea) {
		mapAreaDao.add(mapArea);
	}

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
	public void update(Long brokerId, String apiId, String apiKey, String name, String apiUrl, SmsMethod smsMethod,
			TariffType tariffType) {
		Broker broker = brokerDao.get(brokerId);

		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setApiurl(apiUrl);
		broker.setName(name);
		broker.setSmsMethod(smsMethod);
		broker.setTariffType(tariffType);

		brokerDao.saveOrUpdate(broker);
	}

}
