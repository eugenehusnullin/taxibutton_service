package tb2014.service.tariff;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import tb2014.domain.Broker;

public class TariffsProcessing {

	public static void GetBrokerTariffs() {

		ArrayList<Broker> brokers = new ArrayList<Broker>();

		Broker broker1 = new Broker();
		Broker broker2 = new Broker();

		brokers.add(broker1);
		brokers.add(broker2);

		for (Broker currentBroker : brokers) {

			try {

				Document currentXMLresponce = GetTariffsHTTP(currentBroker);

				currentXMLresponce.getDocumentElement().normalize();
				UpdateBrokerTariffs(currentBroker, currentXMLresponce);
			} catch (Exception e) {

			}

		}
	}

	private static Document GetTariffsHTTP(Broker broker) {

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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	private static void UpdateBrokerTariffs(Broker broker, Document document) {
//
//		String tariffId;
//		String tariffName;
//		NodeList tariffNodes = document.getElementsByTagName("Tariff");
//
//		// looping all tariffs
//		for (int i = 0; i < tariffNodes.getLength(); i++) {
//
//			Node currentTariff = tariffNodes.item(i);
//
//			if (currentTariff.getNodeType() == Node.ELEMENT_NODE) {
//
//				Element currentTariffElement = (Element) currentTariff;
//
//				tariffId = currentTariffElement.getElementsByTagName("Id")
//						.item(0).getNodeValue();
//				tariffName = currentTariffElement.getElementsByTagName("Name")
//						.item(0).getNodeValue();
//
//				Node tariffDescription = currentTariffElement
//						.getElementsByTagName("Description").item(0);
//			}
//
//		}

	}
}
