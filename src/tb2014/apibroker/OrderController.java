package tb2014.apibroker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.domain.order.Car;
import tb2014.domain.order.Driver;
import tb2014.service.OrderService;
import tb2014.service.exceptions.BrokerNotFoundException;
import tb2014.service.exceptions.OrderNotFoundException;
import tb2014.service.exceptions.ParseOrderException;

@RequestMapping("/order")
@Controller("apiBrokerOrderController")
public class OrderController {
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public void alacrity(HttpServletRequest request, HttpServletResponse response) {
		Driver driver = new Driver();
		driver.setName(request.getParameter("driverName") == null ? "�� �������" : request.getParameter("driverName"));
		driver.setSecondName(request.getParameter("driverSecondName") == null ? "�� �������" : request.getParameter("driverSecondName"));
		driver.setThirdName(request.getParameter("driverThirdName") == null ? "�� �������" : request.getParameter("driverThirdName"));
		driver.setPhone(request.getParameter("driverPhone") == null ? "�� �������" : request.getParameter("driverPhone"));

		Car car = new Car();
		car.setNumber(request.getParameter("carNumber") == null ? "�� �������" : request.getParameter("carNumber"));
		car.setColor(request.getParameter("carColor") == null ? "�� �������" : request.getParameter("carColor"));
		car.setMark(request.getParameter("carMark") == null ? "�� �������" : request.getParameter("carMark"));
		car.setModel(request.getParameter("carModel") == null ? "�� �������" : request.getParameter("carModel"));

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
			String status = request.getParameter("status");
			String params = "";

			if (status.equals("Completed")) {
				String timeWay = request.getParameter("timeWay");
				String longWay = request.getParameter("longWay");
				String amount = request.getParameter("amount");
				String amountDriver = request.getParameter("amountDriver");
				params = timeWay + "|" + longWay + "|" + amount + "|" + amountDriver;
			}

			orderService.setStatus(request.getParameter("apiId"), request.getParameter("apiKey"),
					request.getParameter("orderId"), status, params);
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

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void create(HttpServletRequest request, HttpServletResponse response) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(request.getInputStream(), writer);

			JSONObject createOrderObject = (JSONObject) new JSONTokener(writer.toString()).nextValue();
			String newOrderUuuid = orderService.createFromBroker(createOrderObject);

			JSONObject responseJson = new JSONObject();
			responseJson.put("status", "ok");
			responseJson.put("orderId", newOrderUuuid);
			response.setStatus(200);

			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			log.error("api broker - create - input stream error.", e);
			response.setStatus(500);
		} catch (BrokerNotFoundException e) {
			log.error("api broker - create - broker not found.", e);
			response.setStatus(403);
		} catch (ParseOrderException e) {
			log.error("api broker - create - json error.", e);
			response.setStatus(404);
		}
	}
	
	@RequestMapping(value = "/getOrders", method = RequestMethod.GET)
	public void getOrders(HttpServletRequest request, HttpServletResponse response) {
		try {
			String apiId = request.getParameter("apiId");
			String apiKey = request.getParameter("apiKey");
			
			orderService.getOrders(apiId, apiKey);
			
			

			

//			JSONObject responseJson = new JSONObject();
//			responseJson.put("status", "ok");
//			responseJson.put("orderId", newOrderUuuid);
//			response.setStatus(200);
//
//			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
//			outputStream.writeBytes(responseJson.toString());
//			outputStream.flush();
//			outputStream.close();

		} catch (BrokerNotFoundException e) {
			log.error("api broker - create - broker not found.", e);
			response.setStatus(403);
		}
	}
}
