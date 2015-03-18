package tb.car;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
	@Scheduled(cron = "0 58 * * * *")
	public void synch() {
		List<Broker> brokers = brokerDao.getActive();
		for (Broker broker : brokers) {
			try {
				InputStream carsInputStream = fetchCarsInputStream(broker);
				Document doc = XmlUtils.buildDomDocument(carsInputStream);
				if (doc != null) {
					Date loadDate = new Date();
					List<Car> cars = carBuilder.createCars(doc, broker, loadDate);
					updateCars(cars, broker, loadDate);
				}
			} catch (Exception ex) {
				log.error("carsynch", ex);
			}
		}
	}

	private void updateCars(List<Car> cars, Broker broker, Date loadDate) {
		carDao.updateCars(cars, broker, loadDate);
	}

	public InputStream fetchCarsInputStream(Broker broker) {
		try {
			URL url = new URL(broker.getDriverUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");

			if (conn.getResponseCode() == 200) {
				return conn.getInputStream();
			} else {
				log.warn(String.format("Disp - %s return error to CARS request. Error code: %d", broker.getName(),
						conn.getResponseCode()));
			}
		} catch (MalformedURLException e) {
			log.error("url creation error.", e);
		} catch (ProtocolException e) {
			log.error("url creation error.", e);
		} catch (IOException e) {
			log.error("open connection error.", e);
		}
		return null;
	}
}
