package tb2014.car;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tb2014.dao.IBrokerDao;
import tb2014.domain.Broker;

@Service
@EnableScheduling
public class CarSynch {
	private static final Logger log = LoggerFactory.getLogger(CarSynch.class);
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private CarBuilder carBuilder;

	public void synch() {
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			try {
				Document doc = fetchCars(broker);
				if (doc != null) {
					Date loadDate = new Date();
					List<Car> cars = carBuilder.createCars(doc, broker, loadDate);
					updateCars(cars, broker, loadDate);
				}
			} catch (Exception ex) {
				log.info("Get XML cars error: " + ex.toString());
			}

		}
	}

	private void updateCars(List<Car> cars, Broker broker, Date loadDate) {
//		for (Car car : cars) {
//
//		}
	}

	private Document fetchCars(Broker broker) {
		try {
			URL url = new URL(broker.getDriverUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");

			if (conn.getResponseCode() == 200) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(conn.getInputStream());
				return doc;
			} else {
				log.warn(String.format("Disp - %s don't response to CARS request. Error code: %d", broker.getName(),
						conn.getResponseCode()));
			}
		} catch (MalformedURLException e) {
			log.error("url creation error.", e);
		} catch (ProtocolException e) {
			log.error("url creation error.", e);
		} catch (IOException e) {
			log.error("open connection error.", e);
		} catch (ParserConfigurationException e) {
			log.error("create xml builder error.", e);
		} catch (SAXException e) {
			log.error("parse xml error.", e);
		}
		return null;
	}
}
