package tb.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/info")
@Controller("adminInfoController")
public class InfoController {

	@RequestMapping(value = "/tariff", method = RequestMethod.GET)
	public String add(Model model) {
		return "info/tariff";
	}
}
