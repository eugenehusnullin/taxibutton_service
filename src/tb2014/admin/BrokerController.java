package tb2014.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.domain.Broker;
import tb2014.service.BrokerService;
import tb2014.service.TariffService;

@RequestMapping("/broker")
@Controller
public class BrokerController {

	@Autowired
	private BrokerService brokerService;
	@Autowired
	private TariffService tariffService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<Broker> list = brokerService.getAll();
		model.addAttribute("brokers", list);
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
		brokerService.add(broker);

		return "redirect:list";
	}

	@RequestMapping(value = "/tariffs", method = RequestMethod.GET)
	public String tariffs() {

		tariffService.pullBrokersTariffs();
		return "redirect:list";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(@RequestParam("id") Long brokerId) {
		brokerService.delete(brokerId);
		return "redirect:list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam("id") Long brokerId, Model model) {

		Broker broker = brokerService.get(brokerId);

		model.addAttribute("brokerId", brokerId);
		model.addAttribute("apiId", broker.getApiId());
		model.addAttribute("apiKey", broker.getApiKey());
		model.addAttribute("name", broker.getName());
		model.addAttribute("apiUrl", broker.getApiurl());

		return "broker/edit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(@RequestParam("brokerId") Long brokerId, @RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey, @RequestParam("name") String name,
			@RequestParam("apiUrl") String apiUrl) {

		brokerService.update(brokerId, apiId, apiKey, name, apiUrl);
		return "redirect:list";
	}
}
