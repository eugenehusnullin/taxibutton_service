package tb.apiyandex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tb.service.OrderService;
import tb.service.exceptions.BrokerNotFoundException;
import tb.service.exceptions.OrderNotFoundException;

@Controller("apiyandexCarAckController")
@RequestMapping("/carack")
public class CarAckController {
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "")
	public void index(HttpServletRequest request, HttpServletResponse response, @RequestParam("clid") String clid,
			@RequestParam("apikey") String apikey, @RequestParam("uuid") String uuid,
			@RequestParam("orderid") String orderId) {

		try {
			orderService.alacrity(clid, apikey, orderId, uuid);
		} catch (BrokerNotFoundException e) {
			response.setStatus(403);
		} catch (OrderNotFoundException e) {
			response.setStatus(404);
		}
	}
}
