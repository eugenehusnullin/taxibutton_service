package tb2014.tariff;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb2014.domain.Broker;
import tb2014.domain.tariff.Tariff;

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
			tariff.setTariff(tariffElement.getTextContent());

			String id = tariffElement.getElementsByTagName("Id").item(0).getTextContent();
			tariff.setTariffId(id);

			String name = tariffElement.getElementsByTagName("Name").item(0).getTextContent();
			tariff.setName(name);
		}

		return list;
	}
}
