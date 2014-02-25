package tb2014.admin;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import tb2014.business.IBrokerBusiness;
import tb2014.business.ISimpleTariffBusiness;
import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;
import tb2014.utils.ConverterUtil;

@RequestMapping("/tariff")
@Controller
public class SimpleTariffController {

	private ISimpleTariffBusiness simpleTariffBusiness;
	private IBrokerBusiness brokerBusiness;

	@Autowired
	public SimpleTariffController(ISimpleTariffBusiness simpleTariffBusiness, IBrokerBusiness brokerBusiness) {
		this.simpleTariffBusiness = simpleTariffBusiness;
		this.brokerBusiness = brokerBusiness;
	}

	@RequestMapping(value = "/tariff", method = RequestMethod.GET)
	public String tariff(@RequestParam("id") Long brokerId, Model model) {
		model.addAttribute("brokerId", brokerId);

		Broker broker = brokerBusiness.getById(brokerId);
		SimpleTariff tariff = simpleTariffBusiness.get(broker);

		model.addAttribute("tariff", tariff);

		return "tariff/tariff";
	}

	@RequestMapping(value = "/tariff", method = RequestMethod.POST)
	public String tariff(@RequestParam("tariff") String tariff, @RequestParam("brokerId") Long brokerId, Model model) {

		Document doc = null;

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			doc = docBuilder.parse(new InputSource(new StringReader(tariff)));
		} catch (Exception ex) {
			System.out.println("Error parsing DOM: " + ex.toString());
		}

		Broker broker = brokerBusiness.getById(brokerId);
		SimpleTariff simpleTariff = simpleTariffBusiness.get(broker);

		if (simpleTariff == null) {

			simpleTariff = new SimpleTariff();
			simpleTariff.setBroker(broker);
		}

		simpleTariff.setTariffs(ConverterUtil.XmlToString(doc).replace("\r", "").replace("\n", ""));

		simpleTariffBusiness.saveOrUpdate(simpleTariff);

		return "redirect:../broker/list";
	}
}
