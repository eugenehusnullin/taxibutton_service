package tb.tariff;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tb.dao.IBrokerDao;
import tb.dao.ITariffDao;
import tb.domain.Broker;
import tb.domain.Tariff;
import tb.utils.XmlUtils;

@Service
@EnableScheduling
public class TariffSynch {
	private static final Logger log = LoggerFactory.getLogger(TariffSynch.class);
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private TariffBuilder tariffBuilder;
	@Autowired
	private ITariffDao tariffDao;

	@Scheduled(cron = "0 01 * * * *")
	@Transactional
	public void synch() {
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			Document doc = fetchTariffs(broker);
			if (doc != null) {
				Date loadDate = new Date();
				List<Tariff> tariffs;
				try {
					tariffs = tariffBuilder.createTariffs(doc, broker, loadDate);
					updateTariffs(tariffs, broker, loadDate);
				} catch (TransformerFactoryConfigurationError | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void updateTariffs(List<Tariff> tariffs, Broker broker, Date loadDate) {
		for (Tariff tariff : tariffs) {
			tariffDao.saveOrUpdate(tariff);
		}
	}

	private Document fetchTariffs(Broker broker) {
		try {
			if (broker.getTariffUrl() == null || broker.getTariffUrl().isEmpty()) {
				return null;
			}
			URL url = new URL(broker.getTariffUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");

			if (conn.getResponseCode() == 200) {
				Document doc = XmlUtils.buildDomDocument(conn.getInputStream());
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
