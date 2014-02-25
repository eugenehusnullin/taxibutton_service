package tb2014.service.tariff;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import tb2014.Run;
import tb2014.business.IBrokerBusiness;
import tb2014.business.ISimpleTariffBusiness;
import tb2014.utils.ConverterUtil;
import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TariffsProcessing {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	private IBrokerBusiness brokerBusiness;
	private ISimpleTariffBusiness simpleTariffBusiness;

	@Autowired
	public TariffsProcessing(IBrokerBusiness brokerBusiness, ISimpleTariffBusiness simpleTariffBusiness) {

		this.brokerBusiness = brokerBusiness;
		this.simpleTariffBusiness = simpleTariffBusiness;
	}

	public void GetBrokersTariffs() {

		List<Broker> brokers = brokerBusiness.getAll();

		for (Broker currentBroker : brokers) {

			try {

				Document currentXMLResponce = GetTariffsHTTP(currentBroker);
				String currentStringResponce = ConverterUtil.XmlToString(currentXMLResponce);

				UpdateBrokerTariffs(currentBroker, currentStringResponce);
			} catch (Exception ex) {
				log.info("Get XML tariffs error: " + ex.toString());
			}

		}
	}

	private Document GetTariffsHTTP(Broker broker) {

		Document doc = null;
		URL url = null;
		HttpURLConnection connection = null;

		try {

			url = new URL(broker.getApiurl() + "/tariff");
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");

			InputStream xml = connection.getInputStream();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(xml);
		} catch (Exception ex) {
			log.info("Get XML tariffs HTTP error: " + ex.toString());
		}

		return doc;
	}

	private void UpdateBrokerTariffs(Broker broker, String tariff) {

		SimpleTariff simpleTariff = simpleTariffBusiness.get(broker);

		if (simpleTariff == null) {

			simpleTariff = new SimpleTariff();
			simpleTariff.setBroker(broker);
		}

		simpleTariff.setTariffs(tariff);

		simpleTariffBusiness.saveOrUpdate(simpleTariff);
	}
}
