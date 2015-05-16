package tb.tariff;

import java.io.IOException;
import java.io.InputStream;
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
import org.xml.sax.SAXException;

import tb.dao.IBrokerDao;
import tb.dao.ITariffDao;
import tb.domain.Broker;
import tb.domain.Tariff;
import tb.domain.TariffType;
import tb.utils.HttpUtils;

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
		log.info("Start tariff synch.");
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			log.info("Broker - " + broker.getName() + "(" + broker.getApiId() + ")");
			if (broker.getTariffUrl() == null || broker.getTariffUrl().isEmpty()) {
				log.warn("tariff url is empty.");
				continue;
			}

			try {
				InputStream inputStream = HttpUtils.makeGetRequest(broker.getTariffUrl(),
						broker.getTariffType() == TariffType.XML ? "application/xml" : "application/json");
				if (inputStream != null) {
					Date loadDate = new Date();
					List<Tariff> tariffs;

					if (broker.getTariffType() == TariffType.XML) {
						tariffs = tariffBuilder.createTariffsFromXml(inputStream, broker, loadDate);
					} else {
						tariffs = tariffBuilder.createTariffsFromJson(inputStream, broker, loadDate);
					}
					log.info(tariffs.size() + " tariffs - pulled from broker.");
					updateTariffs(tariffs, broker, loadDate);
					log.info("Tariffs saved to db.");
				}
			} catch (TransformerFactoryConfigurationError | TransformerException | ParserConfigurationException
					| SAXException | IOException e) {
				log.error("Tariff synch error: ", e);
			}
		}
	}

	private void updateTariffs(List<Tariff> tariffs, Broker broker, Date loadDate) {
		tariffDao.delete(broker);
		for (Tariff tariff : tariffs) {
			tariffDao.saveOrUpdate(tariff);
		}
	}
}
