package tb2014.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import tb2014.utils.NetStreamUtils;

@Service
public class TariffService {
	
	private static final Logger log = LoggerFactory.getLogger(TariffService.class);

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

		simpleTariff.setTariffs(ConverterUtil.xmlToString(doc).replace("\r", "").replace("\n", ""));
		simpleTariffDao.saveOrUpdate(simpleTariff);
	}
	
	@Transactional
	public void pullBrokersTariffs() {

		List<Broker> brokers = brokerDao.getAll();

		for (Broker currentBroker : brokers) {

			try {

				String currentStringResponce = GetTariffsHTTP(currentBroker);

				UpdateBrokerTariffs(currentBroker, currentStringResponce);
			} catch (Exception ex) {
				log.info("Get XML tariffs error: " + ex.toString());
			}

		}
	}

	private String GetTariffsHTTP(Broker broker) {

		String result = null;
		URL url = null;
		HttpURLConnection connection = null;

		try {

			url = new URL(broker.getApiurl() + "/tariff");
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");

			result = NetStreamUtils.getStringFromInputStream(connection.getInputStream());

		} catch (Exception ex) {
			log.info("Get XML tariffs HTTP error: " + ex.toString());
		}

		return result;
	}

	private void UpdateBrokerTariffs(Broker broker, String tariff) {

		SimpleTariff simpleTariff = simpleTariffDao.get(broker);

		if (simpleTariff == null) {

			simpleTariff = new SimpleTariff();
			simpleTariff.setBroker(broker);
		}

		simpleTariff.setTariffs(tariff);

		simpleTariffDao.saveOrUpdate(simpleTariff);
	}
}
