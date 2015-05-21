package tb.car;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tb.car.domain.Car;
import tb.domain.Broker;
import utils.XmlUtils;

public class CarSynchTest {

	@Test
	public void testSynch() throws ParserConfigurationException, SAXException, IOException {
		URL url = getClass().getResource("/Cars.xml");
		File file = new File(url.getFile());
		FileInputStream fis = new FileInputStream(file);

		Document doc = XmlUtils.buildDomDocument(fis);

		Broker broker = new Broker();
		broker.setId(1L);
		CarBuilder carBuilder = new CarBuilder();
		List<Car> cars = carBuilder.createCars(doc, broker, new Date());

		assertEquals(4, cars.size());

		Car car1 = cars.stream().filter(a -> a.getUuid().equals("uuid01")).findFirst().get();
		assertNotNull(car1);
		assertEquals(3, car1.getTariffs().size());
		assertEquals(6, car1.getCarRequires().size());
		assertNotNull(car1.getRealName());
		assertNotNull(car1.getDriverDisplayName());
		assertTrue(car1.getCarRequires().containsKey("has_conditioner"));
		assertTrue(car1.getCarRequires().get("has_conditioner").equals("yes")
				|| car1.getCarRequires().get("has_conditioner").equals("no"));

	}
}
