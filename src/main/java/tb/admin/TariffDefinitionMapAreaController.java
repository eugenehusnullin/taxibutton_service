package tb.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb.dao.ITariffDefinitionMapAreaDao;
import tb.domain.TariffDefinitionMapArea;

@RequestMapping("/tariffdefmaparea")
@Controller("adminTariffDefinitionMapAreaController")
public class TariffDefinitionMapAreaController {

	@Autowired
	private ITariffDefinitionMapAreaDao tariffDefinitionMapAreaDao;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addMapArea() {
		return "tariffdefmaparea/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addTariffDefinition(HttpServletRequest request) {
		String name = request.getParameter("name");
		String body = request.getParameter("body");
		TariffDefinitionMapArea tariffDefinitionMapArea = new TariffDefinitionMapArea();
		tariffDefinitionMapArea.setName(name);
		tariffDefinitionMapArea.setBody(body);

		tariffDefinitionMapAreaDao.add(tariffDefinitionMapArea);

		return "redirect:list";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listTariffDefinitionMapAreas(Model model) {
		List<TariffDefinitionMapArea> list = tariffDefinitionMapAreaDao.getAll();
		model.addAttribute("tariffdefmapareas", list);
		return "tariffdefmaparea/list";
	}

	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String deleteTariffDefinitionMapArea(@RequestParam("name") String name) {
		tariffDefinitionMapAreaDao.delete(name);

		return "redirect:list";
	}
}