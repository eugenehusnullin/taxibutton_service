package tb2014.dev.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IBrokerBusiness;
import tb2014.business.ISimpleTariffBusiness;
import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;

@RequestMapping("/tariff")
@Controller
public class SimpleTariffController {

	private ISimpleTariffBusiness simpleTariffBusiness;
	private IBrokerBusiness brokerBusiness;

	@Autowired
	public SimpleTariffController(ISimpleTariffBusiness simpleTariffBusiness,
			IBrokerBusiness brokerBusiness) {
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
	public String tariff(@RequestParam("tariff") String tariff,
			@RequestParam("brokerId") Long brokerId, Model model) {
		Broker broker = brokerBusiness.getById(brokerId);
		SimpleTariff simpleTariff = new SimpleTariff();

		simpleTariff.setBroker(broker);
		simpleTariff.setTariffs(tariff);

		simpleTariffBusiness.save(simpleTariff);

		return "redirect:../broker/list";
	}
}
