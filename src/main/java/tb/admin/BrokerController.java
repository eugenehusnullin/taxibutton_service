package tb.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb.domain.Broker;
import tb.domain.SmsMethod;
import tb.domain.TariffType;
import tb.service.BrokerService;
import tb.service.TariffService;

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
			@RequestParam("apiId") String apiId, @RequestParam("apiKey") String apiKey,
			@RequestParam("smsMethod") String smsMethod, @RequestParam("tarifftype") String tariffTypeParam,
			@RequestParam("tariffurl") String tariffUrl,
			@RequestParam("driverurl") String driverUrl,
			@RequestParam("mapareaurl") String mapareaUrl,
			@RequestParam("costurl") String costUrl,
			@RequestParam("timezoneOffset") Integer timezoneOffset,
			Model model) {

		SmsMethod smsM = SmsMethod.values()[Integer.parseInt(smsMethod)];
		TariffType tariffType = TariffType.values()[Integer.parseInt(tariffTypeParam)];

		Broker broker = new Broker();
		broker.setName(name);
		broker.setApiurl(apiurl);
		broker.setApiId(apiId);
		broker.setApiKey(apiKey);
		broker.setSmsMethod(smsM);
		broker.setTariffUrl(tariffUrl);
		broker.setDriverUrl(driverUrl);
		broker.setMapareaUrl(mapareaUrl);
		broker.setCostUrl(costUrl);
		broker.setTariffType(tariffType);
		broker.setTimezoneOffset(timezoneOffset);
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
		model.addAttribute("smsMethod", broker.getSmsMethod() == null ? -1 : broker.getSmsMethod().ordinal());
		model.addAttribute("tarifftype", broker.getTariffType() == null ? -1 : broker.getTariffType().ordinal());
		model.addAttribute("driverUrl", broker.getDriverUrl());
		model.addAttribute("tariffUrl", broker.getTariffUrl());
		model.addAttribute("mapareaUrl", broker.getMapareaUrl());
		model.addAttribute("costUrl", broker.getCostUrl());
		model.addAttribute("timezoneOffset", broker.getTimezoneOffset());

		return "broker/edit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(@RequestParam("brokerId") Long brokerId, @RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey, @RequestParam("name") String name,
			@RequestParam("apiUrl") String apiUrl, @RequestParam("smsMethod") String smsMethod,
			@RequestParam("tarifftype") String tariffTypeParam,
			@RequestParam("tariffUrl") String tariffUrl,
			@RequestParam("driverUrl") String driverUrl,
			@RequestParam("mapareaUrl") String mapareaUrl,
			@RequestParam("costUrl") String costUrl,
			@RequestParam("timezoneOffset") Integer timezoneOffset) {
		SmsMethod smsM = SmsMethod.values()[Integer.parseInt(smsMethod)];
		TariffType tariffType = TariffType.values()[Integer.parseInt(tariffTypeParam)];

		brokerService.update(brokerId, apiId, apiKey, name, apiUrl, smsM, tariffType, tariffUrl, driverUrl,
				timezoneOffset, mapareaUrl, costUrl);
		return "redirect:list";
	}
}
