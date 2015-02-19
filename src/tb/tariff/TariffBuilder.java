package tb.tariff;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb.domain.Broker;
import tb.domain.Tariff;

@Service
public class TariffBuilder {
	public List<Tariff> createTariffs(Document doc, Broker broker, Date loadDate) {
		doc.getDocumentElement().normalize();

		List<Tariff> list = new ArrayList<Tariff>();
		NodeList tariffNodeList = doc.getElementsByTagName("Tariff");
		for (int i = 0; i < tariffNodeList.getLength(); i++) {
			Tariff tariff = new Tariff();
			list.add(tariff);
			tariff.setBroker(broker);
			tariff.setStartDate(loadDate);

			Node tariffNode = tariffNodeList.item(i);
			Element tariffElement = (Element) tariffNode;
			tariff.setTariff(tariffElement.getNodeValue());

			String id = tariffElement.getElementsByTagName("Id").item(0).getNodeValue();
			tariff.setTariffId(id);

			String name = tariffElement.getElementsByTagName("Name").item(0).getNodeValue();
			tariff.setName(name);
		}

		return list;
	}
}
