package tb2014.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import tb2014.dao.IBrokerDao;
import tb2014.dao.IDeviceDao;
import tb2014.dao.ISimpleTariffDao;
import tb2014.domain.Broker;
import tb2014.domain.Device;
import tb2014.domain.tariff.SimpleTariff;
import tb2014.service.exceptions.DeviceNotFoundException;
import tb2014.utils.ConverterUtil;

@Service
public class TariffService {

	@Autowired
	private ISimpleTariffDao simpleTariffDao;
	@Autowired
	private IDeviceDao deviceDao;
	@Autowired
	private IBrokerDao brokerDao;

	@Transactional
	public JSONArray getAll(JSONObject requestJson) throws DeviceNotFoundException {

		String apiId = requestJson.optString("apiId");
		Device device = deviceDao.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		List<SimpleTariff> tariffs = simpleTariffDao.getAll();
		JSONArray tariffsJsonArray = new JSONArray();
		for (SimpleTariff simoleTariff : tariffs) {
			JSONObject tariffJson = new JSONObject();
			tariffJson.put("brokerId", simoleTariff.getBroker().getUuid());
			tariffJson.put("tariff", simoleTariff.getTariffs());
			tariffsJsonArray.put(tariffJson);
		}
		return tariffsJsonArray;
	}

	@Transactional
	public String getTariff(Long brokerId) {
		Broker broker = brokerDao.get(brokerId);
		SimpleTariff simpleTariff = simpleTariffDao.get(broker);
		return simpleTariff.getTariffs();
	}

	@Transactional
	public void create(String tariff, Long brokerId) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(tariff)));

		Broker broker = brokerDao.get(brokerId);
		SimpleTariff simpleTariff = simpleTariffDao.get(broker);

		if (simpleTariff == null) {
			simpleTariff = new SimpleTariff();
			simpleTariff.setBroker(broker);
		}

		simpleTariff.setTariffs(ConverterUtil.XmlToString(doc).replace("\r", "").replace("\n", ""));
		simpleTariffDao.saveOrUpdate(simpleTariff);
	}
}
