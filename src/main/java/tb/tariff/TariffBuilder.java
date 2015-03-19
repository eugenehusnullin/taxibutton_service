package tb.tariff;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tb.domain.Broker;
import tb.domain.Tariff;
import tb.utils.XmlUtils;

@Service
public class TariffBuilder {
	public List<Tariff> createTariffs(Document doc, Broker broker, Date loadDate)
			throws TransformerFactoryConfigurationError, TransformerException {
		List<Tariff> list = new ArrayList<Tariff>();
		doc.getDocumentElement().normalize();

		NodeList tariffNodeList = doc.getElementsByTagName("Tariff");
		for (int i = 0; i < tariffNodeList.getLength(); i++) {
			Tariff tariff = new Tariff();
			list.add(tariff);
			tariff.setBroker(broker);

			Node tariffNode = tariffNodeList.item(i);
			Element tariffElement = (Element) tariffNode;
			tariff.setTariff(tariffElement.getNodeValue());

			tariff.setTariffId(XmlUtils.getElementContent(tariffElement, "Id"));
			tariff.setName(XmlUtils.getElementContent(tariffElement, "Name"));
			tariff.setTariff(XmlUtils.nodeToString(tariffNode));
		}

		return list;
	}
}
