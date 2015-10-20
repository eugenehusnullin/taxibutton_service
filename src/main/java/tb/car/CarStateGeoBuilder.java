package tb.car;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb.car.domain.CarState;
import tb.utils.XmlUtils;

@Service
public class CarStateGeoBuilder {

	public List<CarState> createCarStateGeos(Document doc, Date loadDate) {
		List<CarState> list = new ArrayList<CarState>();

		doc.getDocumentElement().normalize();
		NodeList trackNodeList = doc.getElementsByTagName("track");
		for (int i = 0; i < trackNodeList.getLength(); i++) {
			CarState carState = new CarState();
			carState.setDate(loadDate);

			Node trackNode = trackNodeList.item(i);
			Element trackElement = (Element) trackNode;

			carState.setUuid(trackElement.getAttribute("uuid"));
			Element pointElement = XmlUtils.getOneElement(trackElement, "point");

			carState.setLatitude(Double.parseDouble(pointElement.getAttribute("latitude")));
			carState.setLongitude(Double.parseDouble(pointElement.getAttribute("longitude")));

			list.add(carState);
		}
		return list;
	}

	public String defineCarStateGeosBrokerClid(Document doc) {
		doc.getDocumentElement().normalize();
		Node tracksNode = doc.getElementsByTagName("tracks").item(0);
		Element tracksElement = (Element) tracksNode;
		return tracksElement.getAttribute("clid");
	}
}
