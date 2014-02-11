package tb2014.dev.mvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/admin")
@Controller
public class AdminController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index() {
		return "admin/index";
	}

	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public String orders() {
		return "order/list";
	}

	@RequestMapping(value = "/brokers", method = RequestMethod.GET)
	public String brokers() {
		return "broker/list";
	}

	@RequestMapping(value = "/devices", method = RequestMethod.GET)
	public String devices() {
		return "device/list";
	}
}
