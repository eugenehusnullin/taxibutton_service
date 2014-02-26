package tb2014.admin;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IBrokerBusiness;
import tb2014.domain.Broker;
import tb2014.service.tariff.TariffsProcessing;

@RequestMapping("/broker")
@Controller
public class BrokerController {

	private IBrokerBusiness brokerBusiness;
	private TariffsProcessing tariffsProcessing;

	@Autowired
	public BrokerController(IBrokerBusiness brokerBusiness, TariffsProcessing tariffsProcessing) {
		this.brokerBusiness = brokerBusiness;
		this.tariffsProcessing = tariffsProcessing;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("brokers", brokerBusiness.getAll());
		return "broker/list";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "broker/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(@RequestParam("name") String name, @RequestParam("apiurl") String apiurl,
			@RequestParam("apiId") String apiId, @RequestParam("apiKey") String apiKey, Model model) {

		Broker broker = new Broker();
		broker.setName(name);
		broker.setApiurl(apiurl);
		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setUuid(UUID.randomUUID().toString());
		brokerBusiness.add(broker);

		return "redirect:list";
	}

	@RequestMapping(value = "/tariffs", method = RequestMethod.GET)
	public String tariffs() {

		tariffsProcessing.GetBrokersTariffs();
		return "redirect:list";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(@RequestParam("id") Long brokerId) {

		Broker broker = brokerBusiness.getById(brokerId);
		brokerBusiness.delete(broker);

		return "redirect:list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam("id") Long brokerId, Model model) {

		Broker broker = brokerBusiness.getById(brokerId);

		model.addAttribute("brokerId", brokerId);
		model.addAttribute("apiId", broker.getApiId());
		model.addAttribute("apiKey", broker.getApiKey());
		model.addAttribute("name", broker.getName());
		model.addAttribute("apiUrl", broker.getApiurl());

		return "broker/edit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(@RequestParam("brokerId") Long brokerId, @RequestParam("apiId") String apiId, @RequestParam("apiKey") String apiKey, @RequestParam("name") String name, @RequestParam("apiUrl") String apiUrl) {

		Broker broker = brokerBusiness.getById(brokerId);
		
		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setApiurl(apiUrl);
		broker.setName(name);
		
		brokerBusiness.saveOrUpdate(broker);
		
		return "redirect:list";
	}
}
