package tb2014.apibroker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.domain.order.Car;
import tb2014.domain.order.Driver;
import tb2014.service.OrderService;
import tb2014.service.exceptions.BrokerNotFoundException;
import tb2014.service.exceptions.OrderNotFoundException;

@RequestMapping("/order")
@Controller("apiBrokerOrderController")
public class OrderController {
	// private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public void alacrity(HttpServletRequest request, HttpServletResponse response) {
		Driver driver = new Driver();
		driver.setName(request.getParameter("driverName"));
		driver.setSecondName(request.getParameter("driverSecondName"));
		driver.setThirdName(request.getParameter("driverThirdName"));
		driver.setPhone(request.getParameter("driverPhone"));

		Car car = new Car();
		car.setNumber(request.getParameter("carNumber"));
		car.setColor(request.getParameter("carColor"));
		car.setMark(request.getParameter("carMark"));
		car.setModel(request.getParameter("carModel"));

		try {
			orderService.alacrity(request.getParameter("apiId"), request.getParameter("apiKey"),
					request.getParameter("orderId"), driver, car);
		} catch (BrokerNotFoundException e) {
			response.setStatus(403);
		} catch (OrderNotFoundException e) {
			response.setStatus(404);
		}

		response.setStatus(200);
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
	public void setStatus(HttpServletRequest request, HttpServletResponse response) {
		try {
			orderService.setStatus(request.getParameter("apiId"), request.getParameter("apiKey"),
					request.getParameter("orderId"), request.getParameter("status"));
		} catch (BrokerNotFoundException e) {
			response.setStatus(403);
		} catch (OrderNotFoundException e) {
			response.setStatus(404);
		}
		response.setStatus(200);
	}

	@RequestMapping(value = "/setGeoData", method = RequestMethod.POST)
	public void setGeoData(HttpServletRequest request, HttpServletResponse response) {

		try {
			orderService.setGeoData(request.getParameter("apiId"), request.getParameter("apiKey"),
					request.getParameter("orderId"), request.getParameter("category"),
					request.getParameter("direction"), request.getParameter("lat"), request.getParameter("lon"),
					request.getParameter("speed"));
		} catch (BrokerNotFoundException e) {
			response.setStatus(403);
		} catch (OrderNotFoundException e) {
			response.setStatus(404);
		}

		response.setStatus(200);
	}
}
