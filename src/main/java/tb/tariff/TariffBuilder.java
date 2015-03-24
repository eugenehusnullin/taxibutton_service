package tb.tariff;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tb.domain.Broker;
import tb.domain.Tariff;
import tb.utils.XmlUtils;

@Service
public class TariffBuilder {
	public List<Tariff> createTariffsFromXml(InputStream inputStream, Broker broker, Date loadDate)
			throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException,
			SAXException, IOException {
		Document doc = XmlUtils.buildDomDocument(inputStream);
		doc.getDocumentElement().normalize();
		List<Tariff> list = new ArrayList<Tariff>();
		NodeList tariffNodeList = doc.getElementsByTagName("Tariff");
		for (int i = 0; i < tariffNodeList.getLength(); i++) {
			Tariff tariff = new Tariff();
			list.add(tariff);
			tariff.setBroker(broker);

			Node tariffNode = tariffNodeList.item(i);
			Element tariffElement = (Element) tariffNode;

			tariff.setTariffId(XmlUtils.getElementContent(tariffElement, "Id"));
			tariff.setName(XmlUtils.getElementContent(tariffElement, "Name"));
			tariff.setTariff(XmlUtils.nodeToString(tariffNode));
		}

		return list;
	}

	public List<Tariff> createTariffsFromJson(InputStream inputStream, Broker broker, Date loadDate) throws IOException {
		String tariffsString = IOUtils.toString(inputStream, "utf8");
		JSONArray tariffsArray = (JSONArray) new JSONTokener(tariffsString).nextValue();
		List<Tariff> list = new ArrayList<Tariff>();
		for (int i = 0; i < tariffsArray.length(); i++) {
			Tariff tariff = new Tariff();
			list.add(tariff);
			tariff.setBroker(broker);

			JSONObject jsonTariff = tariffsArray.getJSONObject(i);
			JSONObject jsonTariffClass = jsonTariff.getJSONObject("TariffClass");
			tariff.setTariffId(jsonTariffClass.getString("Id"));
			tariff.setName(jsonTariffClass.getString("DisplayName"));
			tariff.setTariff(jsonTariff.toString());
		}
		return list;
	}
}
