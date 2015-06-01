package tb.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(TariffDefinitionMapAreaController.class);

	@Autowired
	private ITariffDefinitionMapAreaDao tariffDefinitionMapAreaDao;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "tariffdefmaparea/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request) {
		String name = request.getParameter("name");
		String body = request.getParameter("body");
		TariffDefinitionMapArea tariffDefinitionMapArea = new TariffDefinitionMapArea();
		tariffDefinitionMapArea.setName(name);
		tariffDefinitionMapArea.setBody(body);

		tariffDefinitionMapAreaDao.add(tariffDefinitionMapArea);

		return "redirect:list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam("name") String name, Model model) {
		TariffDefinitionMapArea tariffDefinitionMapArea = tariffDefinitionMapAreaDao.get(name);
		if (tariffDefinitionMapArea == null) {
			logger.warn("TariffDefinitionMapArea not found, name=" + name);
			return "redirect:list";
		}
		model.addAttribute("name", name);
		model.addAttribute("body", tariffDefinitionMapArea.getBody());
		return "tariffdefmaparea/edit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(HttpServletRequest request) {
		String oldName = request.getParameter("oldname");
		String name = request.getParameter("name");
		String body = request.getParameter("body");

		TariffDefinitionMapArea tariffDefinitionMapArea = tariffDefinitionMapAreaDao.get(oldName);
		tariffDefinitionMapArea.setName(name);
		tariffDefinitionMapArea.setBody(body);

		tariffDefinitionMapAreaDao.update(tariffDefinitionMapArea);

		return "redirect:list";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<TariffDefinitionMapArea> list = tariffDefinitionMapAreaDao.getAll();
		model.addAttribute("tariffdefmapareas", list);
		return "tariffdefmaparea/list";
	}

	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String del(@RequestParam("name") String name) {
		tariffDefinitionMapAreaDao.delete(name);

		return "redirect:list";
	}
}
