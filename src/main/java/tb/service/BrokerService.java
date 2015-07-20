package tb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.IBrokerDao;
import tb.domain.Broker;
import tb.domain.SmsMethod;
import tb.domain.TariffType;
import tb.domain.maparea.MapArea;

@Service
public class BrokerService {

	@Autowired
	private IBrokerDao brokerDao;
	// @Autowired
	// private IMapAreaDao mapAreaDao;
	@Autowired
	private MapAreaAssist mapAreaAssist;

	@Transactional
	public List<Broker> getAll() {
		return brokerDao.getAll();
	}

	@Transactional
	public List<Broker> getBrokersByMapAreas(double lat, double lon) {
		List<Broker> allBrokers = brokerDao.getAll();
		List<Broker> result = new ArrayList<Broker>();
		for (Broker broker : allBrokers) {
			for (MapArea mapArea : broker.getMapAreas()) {
				if (mapAreaAssist.contains(mapArea, lat, lon)) {
					result.add(broker);
					break;
				}
			}
		}
		return result;
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
			TariffType tariffType, String tariffUrl, String driverUrl, Integer timezoneOffset, String mapareaUrl,
			String costUrl, Set<MapArea> mapAreasSet) {
		Broker broker = brokerDao.get(brokerId);

		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setApiurl(apiUrl);
		broker.setName(name);
		broker.setSmsMethod(smsMethod);
		broker.setTariffType(tariffType);
		broker.setTariffUrl(tariffUrl);
		broker.setDriverUrl(driverUrl);
		broker.setTimezoneOffset(timezoneOffset);
		broker.setMapareaUrl(mapareaUrl);
		broker.setCostUrl(costUrl);
		broker.setMapAreas(mapAreasSet);
		brokerDao.saveOrUpdate(broker);
	}

	@Transactional
	public Broker getByUuid(String uuid) {
		return brokerDao.get(uuid);
	}
}
