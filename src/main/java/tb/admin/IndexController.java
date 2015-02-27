package tb.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@RequestMapping("/index")
@Controller
public class IndexController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index() {

		return "admin/index";
	}
}
