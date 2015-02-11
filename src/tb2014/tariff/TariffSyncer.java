package tb2014.tariff;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tb2014.dao.IBrokerDao;
import tb2014.dao.ITariffDao;
import tb2014.domain.Broker;
import tb2014.domain.Tariff;

@Service
@EnableScheduling
public class TariffSyncer {
	private static final Logger log = LoggerFactory.getLogger(TariffSyncer.class);
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private TariffBuilder tariffBuilder;
	@Autowired
	private ITariffDao tariffDao;

	@Scheduled(cron = "0 01 * * * *")
	@Transactional
	public void sync() {
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			try {
				Document doc = fetchTariffs(broker);
				if (doc != null) {
					Date loadDate = new Date();
					List<Tariff> tariffs = tariffBuilder.createTariffs(doc, broker, loadDate);
					updateTariffs(tariffs, broker, loadDate);
				}
			} catch (Exception ex) {
				log.info("Get XML tariffs error: " + ex.toString());
			}

		}
	}
	
	private void updateTariffs(List<Tariff> tariffs, Broker broker, Date loadDate) {
		for (Tariff tariff : tariffs) {
			Tariff storedTariff = tariffDao.getActive(broker, tariff.getTariffId());
			if (storedTariff == null) {
				tariffDao.save(tariff);
			} else {
				if (!storedTariff.isSame(tariff)) {
					storedTariff.setEndDate(loadDate);
					tariffDao.save(storedTariff);
					tariffDao.save(tariff);
				}
			}
		}
	}

	private Document fetchTariffs(Broker broker) {
		try {
			URL url = new URL(broker.getTariffUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");

			if (conn.getResponseCode() == 200) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(conn.getInputStream());
				return doc;
			} else {
				log.warn(String.format("Disp - %s don't response to tariff request. Error code: %d", broker.getName(),
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
