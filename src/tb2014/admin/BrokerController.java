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

@RequestMapping("/broker")
@Controller
public class BrokerController {

	private IBrokerBusiness brokerBusiness;

	@Autowired
	public BrokerController(IBrokerBusiness brokerBusiness) {
		this.brokerBusiness = brokerBusiness;
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

}
