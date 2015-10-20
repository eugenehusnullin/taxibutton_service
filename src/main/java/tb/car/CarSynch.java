package tb.car;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import tb.car.dao.CarDao;
import tb.car.domain.Car;
import tb.dao.IBrokerDao;
import tb.domain.Broker;
import tb.utils.HttpUtils;
import tb.utils.XmlUtils;

@Service
@EnableScheduling
public class CarSynch {
	private static final Logger logger = LoggerFactory.getLogger(CarSynch.class);
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private CarBuilder carBuilder;
	@Autowired
	private CarDao carDao;

	@Transactional
	@Scheduled(cron = "0 01 * * * *")
	public void synch() {
		logger.info("Start car synch.");
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			logger.info("Broker - " + broker.getName() + "(" + broker.getApiId() + ")");
			try {
				InputStream carsInputStream = HttpUtils.makeGetRequest(broker.getDriverUrl(), "application/xml");
				Document doc = XmlUtils.buildDomDocument(carsInputStream);
				Date loadDate = new Date();
				List<Car> cars = carBuilder.createCars(doc, broker, loadDate);
				logger.info(cars.size() + " cars - pulled from broker.");
				updateCars(cars, broker, loadDate);
				logger.info("Cars saved to db.");
			} catch (Exception e) {
				logger.error("Broker - " + broker.getName() + "(" + broker.getApiId() + ")" + " Car synch error: ", e);
			}
		}
		logger.info("End car synch.");
	}

	private void updateCars(List<Car> cars, Broker broker, Date loadDate) {
		carDao.updateCars(cars, broker, loadDate);
	}
}
