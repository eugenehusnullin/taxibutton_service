package tb.car;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb.car.domain.Car;
import tb.domain.Broker;

@Service
public class CarBuilder {

	public Map<String, Car> createCars(Document doc, Broker broker, Date loadDate) {
		Map<String, Car> map = new HashMap<String, Car>();

		doc.getDocumentElement().normalize();
		NodeList carNodeList = doc.getElementsByTagName("Car");
		for (int i = 0; i < carNodeList.getLength(); i++) {
			Car car = new Car();
			car.setBrokerId(broker.getId());

			Node carNode = carNodeList.item(i);
			Element carElement = (Element) carNode;

			car.setUuid(getElementContent(carElement, "Uuid"));
			car.setRealClid(getElementContent(carElement, "RealClid"));
			car.setRealName(getElementContent(carElement, "RealName"));
			car.setRealWeb(getElementContent(carElement, "RealWeb"));
			car.setRealScid(getElementContent(carElement, "RealScid"));

			NodeList tariffNodeList = carElement.getElementsByTagName("Tariff");
			for (int j = 0; j < tariffNodeList.getLength(); j++) {
				Node tariffNode = tariffNodeList.item(j);
				Element tariffElement = (Element) tariffNode;
				car.getTariffList().add(tariffElement.getFirstChild().getNodeValue());
			}

			Element driverDetailsElement = getOneElement(carElement, "DriverDetails");
			if (driverDetailsElement != null) {
				car.setDriverDisplayName(getElementContent(driverDetailsElement, "DisplayName"));
				car.setDriverPhone(getElementContent(driverDetailsElement, "Phone"));
				car.setDriverAge(Integer.parseInt(getElementContent(driverDetailsElement, "Age")));
				car.setDriverLicense(getElementContent(driverDetailsElement, "DriverLicense"));
				car.setDriverPermit(getElementContent(driverDetailsElement, "Permit"));
			}

			Element carDetailsElement = getOneElement(carElement, "CarDetails");
			car.setCarModel(getElementContent(carDetailsElement, "Model"));
			String carAge = getElementContent(carDetailsElement, "Age");
			car.setCarAge(carAge == null ? null : Integer.parseInt(carAge));
			car.setCarColor(getElementContent(carDetailsElement, "Color"));
			car.setCarNumber(getElementContent(carDetailsElement, "CarNumber"));
			car.setCarPermit(getElementContent(carDetailsElement, "Permit"));

			NodeList carRequireNodeList = carDetailsElement.getElementsByTagName("Require");
			for (int j = 0; j < carRequireNodeList.getLength(); j++) {
				String requireValue = carRequireNodeList.item(j).getFirstChild().getNodeValue();
				String requireName = ((Element) carRequireNodeList.item(j)).getAttribute("name");
				car.getCarRequires().put(requireName, requireValue);
			}

			map.put(car.getUuid(), car);
		}
		return map;
	}

	private String getElementContent(Element element, String elementName) {
		NodeList nodeList = element.getElementsByTagName(elementName);
		if (nodeList != null && nodeList.getLength() > 0) {
			return nodeList.item(0).getFirstChild().getNodeValue();
		} else {
			return null;
		}
	}

	private Element getOneElement(Element element, String elementName) {
		NodeList nodeList = element.getElementsByTagName(elementName);
		if (nodeList != null && nodeList.getLength() == 1) {
			return (Element) nodeList.item(0);
		} else {
			return null;
		}
	}
}
