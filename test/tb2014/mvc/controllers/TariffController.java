package tb2014.mvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TariffController {
	
	@RequestMapping(value = "/tariff", method = RequestMethod.GET)
	public String tariff() {
		return null;
	}
}
