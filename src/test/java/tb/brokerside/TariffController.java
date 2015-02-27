package tb.brokerside;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/tariff")
@Controller("brokerSideTariffController")
public class TariffController {

	private static final Logger log = LoggerFactory.getLogger(TariffController.class);

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Document tariff() {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element tariffs = doc.createElement("Tariffs");

			doc.appendChild(tariffs);

			for (int i = 0; i < 2; i++) {

				Element tariff = doc.createElement("Tariff");

				tariffs.appendChild(tariff);

				// set childs to tariff element
				Element id = doc.createElement("Id");
				id.appendChild(doc.createTextNode(Integer.toString(i)));
				tariffs.appendChild(id);

				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode("name" + Integer.toString(i)));
				tariffs.appendChild(name);

				Element description = doc.createElement("Descriptions");
				description.appendChild(doc.createTextNode("description" + Integer.toString(i)));
				tariffs.appendChild(description);
			}

			return doc;
		} catch (Exception ex) {

			log.info(ex.toString());
		}

		return null;
	}
}
