package tb.car;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.w3c.dom.Document;

import tb.car.domain.Car;
import tb.domain.Broker;

public class CarSynchTest {

	@Test
	public void testSynch() throws FileNotFoundException {
		URL url = getClass().getResource("/Cars.xml");
		File file = new File(url.getFile());
		FileInputStream fis = new FileInputStream(file);
		
		CarSynch carSynch = new CarSynch();
		Document doc = carSynch.buildDomDocument(fis);
		
		Broker broker = new Broker();
		broker.setId(1L);
		CarBuilder carBuilder = new CarBuilder();
		Map<String, Car> cars = carBuilder.createCars(doc, broker, new Date());
		
	}
}
