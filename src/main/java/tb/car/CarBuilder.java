package tb.car;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb.car.domain.Car;
import tb.domain.Broker;
import utils.XmlUtils;

@Service
public class CarBuilder {

	public List<Car> createCars(Document doc, Broker broker, Date loadDate) {
		List<Car> list = new ArrayList<Car>();
		doc.getDocumentElement().normalize();

		NodeList carNodeList = doc.getElementsByTagName("Car");
		for (int i = 0; i < carNodeList.getLength(); i++) {
			Car car = new Car();
			car.setBrokerId(broker.getId());

			Node carNode = carNodeList.item(i);
			Element carElement = (Element) carNode;

			car.setUuid(XmlUtils.getElementContent(carElement, "Uuid"));
			car.setRealClid(XmlUtils.getElementContent(carElement, "RealClid"));
			car.setRealName(XmlUtils.getElementContent(carElement, "RealName"));
			car.setRealWeb(XmlUtils.getElementContent(carElement, "RealWeb"));
			car.setRealScid(XmlUtils.getElementContent(carElement, "RealScid"));

			NodeList tariffNodeList = carElement.getElementsByTagName("Tariff");
			for (int j = 0; j < tariffNodeList.getLength(); j++) {
				Node tariffNode = tariffNodeList.item(j);
				Element tariffElement = (Element) tariffNode;
				car.getTariffs().add(tariffElement.getFirstChild().getNodeValue());
			}

			Element driverDetailsElement = XmlUtils.getOneElement(carElement, "DriverDetails");
			if (driverDetailsElement != null) {
				car.setDriverDisplayName(XmlUtils.getElementContent(driverDetailsElement, "DisplayName"));
				car.setDriverPhone(XmlUtils.getElementContent(driverDetailsElement, "Phone"));
				car.setDriverAge(Integer.parseInt(XmlUtils.getElementContent(driverDetailsElement, "Age")));
				car.setDriverLicense(XmlUtils.getElementContent(driverDetailsElement, "DriverLicense"));
				car.setDriverPermit(XmlUtils.getElementContent(driverDetailsElement, "Permit"));
			}

			Element carDetailsElement = XmlUtils.getOneElement(carElement, "CarDetails");
			car.setCarModel(XmlUtils.getElementContent(carDetailsElement, "Model"));
			String carAge = XmlUtils.getElementContent(carDetailsElement, "Age");
			car.setCarAge(carAge == null ? null : Integer.parseInt(carAge));
			car.setCarColor(XmlUtils.getElementContent(carDetailsElement, "Color"));
			car.setCarNumber(XmlUtils.getElementContent(carDetailsElement, "CarNumber"));
			car.setCarPermit(XmlUtils.getElementContent(carDetailsElement, "Permit"));

			NodeList carRequireNodeList = carDetailsElement.getElementsByTagName("Require");
			for (int j = 0; j < carRequireNodeList.getLength(); j++) {
				String requireValue = carRequireNodeList.item(j).getFirstChild().getNodeValue();
				String requireName = ((Element) carRequireNodeList.item(j)).getAttribute("name");
				car.getCarRequires().put(requireName, requireValue);
			}

			list.add(car);
		}
		return list;
	}

}
