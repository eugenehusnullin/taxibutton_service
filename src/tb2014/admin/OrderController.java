package tb2014.admin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.admin.model.AlacrityModel;
import tb2014.admin.model.GeodataModel;
import tb2014.admin.model.OrderModel;
import tb2014.admin.model.OrderStatusModel;
import tb2014.service.BrokerService;
import tb2014.service.GeodataService;
import tb2014.service.OrderService;

@RequestMapping("/order")
@Controller("devTestOrderController")
public class OrderController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private GeodataService geodataService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(HttpServletRequest request, Model model) {

		String orderField = null;
		String orderDirection = null;
		int start = 0;
		int count = 0;

		if (request.getParameter("orderField") == null) {
			orderField = "bookingDate";
		} else {
			orderField = request.getParameter("orderField");
		}

		if (request.getParameter("orderDirection") == null) {
			orderDirection = "desc";
		} else {
			orderDirection = request.getParameter("orderDirection");
		}

		if (request.getParameter("start") == null) {
			start = 0;
		} else {
			start = Integer.parseInt(request.getParameter("start"));
		}

		if (request.getParameter("count") == null) {
			count = 10;
		} else {
			count = Integer.parseInt(request.getParameter("count"));
		}

		List<OrderModel> orderList = orderService.listByPage(orderField, orderDirection, start, count);
		Long allOrdersCount = orderService.getAllCount();

		int pagesCount = (int) Math.ceil(allOrdersCount / (double) count);
		int[] pages = new int[pagesCount];

		for (int i = 0; i < pagesCount; i++) {
			pages[i] = i + 1;
		}

		model.addAttribute("orders", orderList);
		model.addAttribute("pages", pages);
		model.addAttribute("orderField", orderField);
		model.addAttribute("orderDirection", orderDirection);
		model.addAttribute("start", start);
		model.addAttribute("count", count);
		return "order/list";
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.GET)
	public String alacrity(@RequestParam("id") Long orderId, Model model) {
		List<AlacrityModel> listAlacrity = orderService.getAlacrities(orderId);

		model.addAttribute("alacrities", listAlacrity);
		model.addAttribute("orderId", orderId);

		return "order/alacrity";
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public String alacrity(HttpServletRequest request) throws IOException {

		String url = "http://localhost:8080/tb2014/apibroker/order/alacrity";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		
		OrderModel orderModel = orderService.getOrder(Long.parseLong(request.getParameter("orderId")));

		String params = "orderId=" + orderModel.getUuid() + "&apiId=" + request.getParameter("apiId")
				+ "&apiKey=" + request.getParameter("apiKey") + "&driverName=" + request.getParameter("driverName")
				+ "&driverSecondName=" + request.getParameter("driverSecondName") + "&driverThirdName="
				+ request.getParameter("driverThirdName") + "&driverPhone=" + request.getParameter("driverPhone")
				+ "&carNumber=" + request.getParameter("carNumber") + "&carColor=" + request.getParameter("carColor")
				+ "&carMark=" + request.getParameter("carMark") + "&carModel=" + request.getParameter("carModel");

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));

		connection.setDoOutput(true);

		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

		outputStream.writeBytes(params);
		outputStream.flush();
		outputStream.close();

		@SuppressWarnings("unused")
		int responseCode = connection.getResponseCode();

		return "redirect:list";
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.GET)
	public String sendStatus(@RequestParam("id") Long orderId, Model model) {
		model.addAttribute("orderId", orderId);
		return "order/setStatus";
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
	public String sendStatus(@RequestParam("orderId") Long orderId, @RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey, @RequestParam("status") String status) throws IOException {

		OrderModel orderModel = orderService.getOrder(orderId);

		String url = "http://localhost:8080/tb2014/apibroker/order/setStatus";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		String params = "orderId=" + orderModel.getUuid() + "&apiId=" + apiId + "&apiKey=" + apiKey + "&status="
				+ status;

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));

		connection.setDoOutput(true);

		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

		outputStream.writeBytes(params);
		outputStream.flush();
		outputStream.close();

		@SuppressWarnings("unused")
		int responseCode = connection.getResponseCode();

		return "redirect:list";
	}

	@RequestMapping(value = "/setGeoData", method = RequestMethod.GET)
	public String setGeoData(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/setGeoData";
	}

	@RequestMapping(value = "/setGeoData", method = RequestMethod.POST)
	public String setGeoData(HttpServletRequest request) throws IOException {

		String url = "http://localhost:8080/tb2014/apibroker/order/setGeoData";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		String params = "orderId=" + request.getParameter("orderId") + "&apiId=" + request.getParameter("apiId")
				+ "&apiKey=" + request.getParameter("apiKey") + "&lon=" + request.getParameter("lon") + "&lat="
				+ request.getParameter("lat");

		if (request.getParameter("direction") != null) {
			params += "&direction=" + request.getParameter("direction");
		}

		if (request.getParameter("speed") != null) {
			params += "&speed=" + request.getParameter("speed");
		}

		if (request.getParameter("category") != null) {
			params += "&category=" + request.getParameter("category");
		}

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));

		connection.setDoOutput(true);

		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

		outputStream.writeBytes(params);
		outputStream.flush();
		outputStream.close();

		@SuppressWarnings("unused")
		int responseCode = connection.getResponseCode();

		return "redirect:list";
	}

	@RequestMapping(value = "/getStatus", method = RequestMethod.GET)
	public String getStatus(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/getStatus";
	}

	@RequestMapping(value = "/getStatus", method = RequestMethod.POST)
	public String getStatus(@RequestParam("orderId") Long orderId, @RequestParam("apiId") String apiId, Model model) {

		OrderModel orderModel = orderService.getOrder(orderId);
		JSONObject getStatusJson = new JSONObject();

		getStatusJson.put("apiId", apiId);
		getStatusJson.put("orderId", orderModel.getUuid());

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/status";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(getStatusJson.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				model.addAttribute("result",
						"Error sending order to server: " + getStringFromInputStream(connection.getInputStream()));
			} else {
				model.addAttribute("result", getStringFromInputStream(connection.getInputStream()));
			}

		} catch (Exception ex) {
			model.addAttribute("result", "Error getting order status: " + ex.toString());
		}

		return "result";
	}

	@RequestMapping(value = "/getGeoData", method = RequestMethod.GET)
	public String getGeoData(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/getGeoData";
	}

	@RequestMapping(value = "/getGeoData", method = RequestMethod.POST)
	public String getGeoData(@RequestParam("orderId") Long orderId, @RequestParam("apiId") String apiId,
			@RequestParam("lastDate") String lastDate, Model model) {

		OrderModel orderModel = orderService.getOrder(orderId);
		JSONObject getGeoDataJson = new JSONObject();

		getGeoDataJson.put("apiId", apiId);
		getGeoDataJson.put("orderId", orderModel.getUuid());
		getGeoDataJson.put("lastDate", lastDate);

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/geodata";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(getGeoDataJson.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error sending order to server");
			}

			model.addAttribute("result", getStringFromInputStream(connection.getInputStream()));

		} catch (Exception ex) {
			System.out.println("Error getting order status: " + ex.toString());
		}

		return "result";
	}

	@RequestMapping(value = "/showStatus", method = RequestMethod.GET)
	public String showStatus(@RequestParam("id") Long orderId, Model model) {
		List<OrderStatusModel> statusList = orderService.getStatuses(orderId);
		model.addAttribute("statusList", statusList);

		return "order/statusList";
	}

	@RequestMapping(value = "/showGeoData", method = RequestMethod.GET)
	public String showGeoData(@RequestParam("id") Long orderId, Model model) {

		List<GeodataModel> list = geodataService.getGeodata(orderId);
		model.addAttribute("geoList", list);
		return "order/geoList";
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public String cancel(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/cancel";
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public String cancel(HttpServletRequest request, Model model) {

		JSONObject cancelOrderJson = new JSONObject();
		OrderModel orderModel = orderService.getOrder(Long.parseLong(request.getParameter("orderId")));

		cancelOrderJson.put("apiId", request.getParameter("apiId"));
		cancelOrderJson.put("orderId", orderModel.getUuid());
		cancelOrderJson.put("reason", request.getParameter("reason"));

		String jsonResult = cancelOrderJson.toString();
		int responseCode = 0;

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/cancel";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(jsonResult);
			wr.flush();
			wr.close();

			responseCode = connection.getResponseCode();

			if (responseCode != 200) {
				System.out.println("Error cancelling order");
			}
		} catch (Exception ex) {
			System.out.println("Error cancelling order (client): " + ex.toString());
		}

		model.addAttribute("result", "Response code is: " + responseCode);
		return "result";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {

		model.addAttribute("brokers", brokerService.getAll());
		return "order/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(HttpServletRequest request, Model model) {

		JSONObject createOrderJson = new JSONObject();

		createOrderJson.put("apiId", request.getParameter("apiId"));

		JSONObject orderJson = new JSONObject();

		orderJson.put("recipientBlackListed", "no");
		orderJson.put("recipientLoyal", "yes");
		orderJson.put("recipientPhone", request.getParameter("phone"));

		JSONObject sourceJson = new JSONObject();

		sourceJson.put("lon", request.getParameter("sourceLon"));
		sourceJson.put("lat", request.getParameter("sourceLat"));
		sourceJson.put("fullAddress", request.getParameter("sFullAddress"));
		sourceJson.put("shortAddress", request.getParameter("sShortAddress"));
		sourceJson.put("closestStation", request.getParameter("sClosestStation"));
		sourceJson.put("country", request.getParameter("sCountry"));
		sourceJson.put("locality", request.getParameter("sLocality"));
		sourceJson.put("street", request.getParameter("sStreet"));
		sourceJson.put("housing", request.getParameter("sHousing"));

		orderJson.put("source", sourceJson);

		JSONArray destinationsJson = new JSONArray();
		JSONObject destinationJson = null;

		if (!request.getParameter("dFullAddress").isEmpty()) {
			destinationJson = new JSONObject();

			destinationJson.put("index", "1");
			destinationJson.put("lon", request.getParameter("destinationLon"));
			destinationJson.put("lat", request.getParameter("destinationLat"));
			destinationJson.put("fullAddress", request.getParameter("dFullAddress"));
			destinationJson.put("shortAddress", request.getParameter("dShortAddress"));
			destinationJson.put("closestStation", request.getParameter("dClosestStation"));
			destinationJson.put("country", request.getParameter("dCountry"));
			destinationJson.put("locality", request.getParameter("dLocality"));
			destinationJson.put("street", request.getParameter("dStreet"));
			destinationJson.put("housing", request.getParameter("dHousing"));
			destinationsJson.put(destinationJson);
		}

		orderJson.put("destinations", destinationsJson);
		orderJson.put("urgent", request.getParameter("orderType"));
		orderJson.put("bookingDate", request.getParameter("bookingDate"));

		JSONArray requirementsJson = new JSONArray();

		String[] formRequirements = request.getParameterValues("requirements");

		for (String currentRequirementName : formRequirements) {

			JSONObject currentRequirementJson = new JSONObject();

			currentRequirementJson.put("name", currentRequirementName);

			if (currentRequirementName.trim().equals("isChildChair")) {
				currentRequirementJson.put("value", request.getParameter("childAge"));
			} else {
				currentRequirementJson.put("value", "yes");
			}

			requirementsJson.put(currentRequirementJson);
		}

		orderJson.put("requirements", requirementsJson);

		String[] brokers = request.getParameterValues("brokers");

		orderJson.put("vehicleClass", request.getParameter("vehicleClass"));
		orderJson.put("brokers", brokers);

		createOrderJson.put("order", orderJson);

		String jsonResult = createOrderJson.toString();

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/create";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(jsonResult);
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				model.addAttribute("result", "Error sending order request to server, code: " + responceCode);
			} else {
				// need to get an response with order id
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;

				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
							connection.getInputStream()));

					while ((line = bufferedReader.readLine()) != null) {
						stringBuffer.append(line);
					}
				} catch (Exception ex) {
					model.addAttribute("result", "Error receiving server respone: " + ex.toString());
				}

				JSONObject responseJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();

				System.out.println("Server JSON response: " + responseJson.toString());
				model.addAttribute("result", "Order created successfully");
			}
		} catch (Exception ex) {
			model.addAttribute("result", "Error creating new order: " + ex.toString());
		}

		return "result";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(@RequestParam("id") Long orderId) {
		orderService.deleteOrder(orderId);
		return "redirect:list";
	}

	private String getStringFromInputStream(InputStream stream) {

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception ex) {
			System.out.println("Error greating string from input stream: " + ex.toString());
		}

		return stringBuffer.toString();
	}
}
