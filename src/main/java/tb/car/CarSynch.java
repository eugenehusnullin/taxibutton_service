package tb.car;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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

import tb.car.dao.CarDao;
import tb.car.domain.Car;
import tb.dao.IBrokerDao;
import tb.domain.Broker;
import tb.utils.HttpUtils;
import tb.utils.XmlUtils;

@Service
@EnableScheduling
public class CarSynch {
	private static final Logger log = LoggerFactory.getLogger(CarSynch.class);
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private CarBuilder carBuilder;
	@Autowired
	private CarDao carDao;

	@Transactional
	@Scheduled(cron = "0 01 * * * *")
	public void synch() {
		log.info("Start car synch.");
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			log.info("Broker - " + broker.getName() + "(" + broker.getApiId() + ")");
			try {
				InputStream carsInputStream = HttpUtils.makeGetRequest(broker.getDriverUrl(), "application/xml");
				Document doc = XmlUtils.buildDomDocument(carsInputStream);
				Date loadDate = new Date();
				List<Car> cars = carBuilder.createCars(doc, broker, loadDate);
				log.info(cars.size() + " cars - pulled from broker.");
				updateCars(cars, broker, loadDate);
				log.info("Cars saved to db.");
			} catch (ParserConfigurationException | SAXException | IOException e) {
				log.error("Car synch error: ", e);
			}
		}
		log.info("End car synch.");
	}

	private void updateCars(List<Car> cars, Broker broker, Date loadDate) {
		carDao.updateCars(cars, broker, loadDate);
	}
}
