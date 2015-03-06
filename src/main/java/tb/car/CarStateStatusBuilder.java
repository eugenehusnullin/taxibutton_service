package tb.car;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb.car.domain.CarState;
import tb.car.domain.CarStateEnum;

@Service
public class CarStateStatusBuilder {
	public List<CarState> createCarStateStatuses(Document doc) {
		List<CarState> list = new ArrayList<CarState>();

		doc.getDocumentElement().normalize();
		NodeList carNodeList = doc.getElementsByTagName("Car");
		for (int i = 0; i < carNodeList.getLength(); i++) {
			CarState carState = new CarState();
			Node carStateNode = carNodeList.item(i);
			Element carStateElement = (Element) carStateNode;

			carState.setUuid(carStateElement.getAttribute("uuid"));
			String stateString = carStateElement.getAttribute("status");
			carState.setState(defineCarState(stateString));

			list.add(carState);
		}
		return list;
	}

	public static CarStateEnum defineCarState(String status) {
		CarStateEnum carStateEnum;
		switch (status) {
		case "free":
			carStateEnum = CarStateEnum.Free;
			break;

		case "busy":
			carStateEnum = CarStateEnum.Busy;
			break;

		case "verybusy":
			carStateEnum = CarStateEnum.VeryBusy;
			break;

		default:
			carStateEnum = CarStateEnum.Undefined;
			break;
		}

		return carStateEnum;
	}
}
