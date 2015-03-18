package tb.apiyandex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tb.service.OrderService;
import tb.service.exceptions.BrokerNotFoundException;
import tb.service.exceptions.OrderNotFoundException;

@Controller("apiyandexRequestConfirmController")
@RequestMapping("requestconfirm")
// Обновление статуса заказа
public class RequestConfirmController {
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		String clid = request.getParameter("clid");
		String apikey = request.getParameter("apikey");
		String orderId = request.getParameter("orderid");
		String status = request.getParameter("status");
		String extra = request.getParameter("extra");
		String newcar = request.getParameter("newcar");

		try {
			if (newcar != null) {
				orderService.setNewcar(clid, apikey, orderId, newcar);
			} else {
				orderService.setStatus(clid, apikey, orderId, status, extra);
			}
		} catch (BrokerNotFoundException e) {
			response.setStatus(403);
		} catch (OrderNotFoundException e) {
			response.setStatus(404);
		}
	}
}
