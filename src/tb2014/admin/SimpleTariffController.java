package tb2014.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import tb2014.service.BrokerService;
import tb2014.service.TariffService;

@RequestMapping("/tariff")
@Controller
public class SimpleTariffController {

	@Autowired
	private BrokerService brokerService;
	@Autowired
	private TariffService tariffService;

	@RequestMapping(value = "/tariff", method = RequestMethod.GET)
	public String tariff(@RequestParam("id") Long brokerId, Model model) {

		String tariff = tariffService.getTariff(brokerId);
		model.addAttribute("tariff", tariff);
		model.addAttribute("brokerId", brokerId);
		return "tariff/tariff";
	}

	@RequestMapping(value = "/tariff", method = RequestMethod.POST)
	public String tariff(@RequestParam("tariff") String tariff, @RequestParam("brokerId") Long brokerId, HttpServletResponse response) {
		try {
			tariffService.create(tariff, brokerId);
		} catch (ParserConfigurationException e) {
			response.setStatus(404);
		} catch (SAXException e) {
			response.setStatus(404);
		} catch (IOException e) {
			response.setStatus(404);
		}

		return "redirect:../broker/list";
	}
}
