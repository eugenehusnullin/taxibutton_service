package tb.admin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb.admin.model.AlacrityModel;
import tb.admin.model.OrderModel;
import tb.admin.model.OrderStatusModel;
import tb.domain.Broker;
import tb.domain.order.Order;
import tb.domain.order.OrderStatusType;
import tb.service.BrokerService;
import tb.service.OrderService;
import utils.DatetimeUtils;
import utils.HttpUtils;

@RequestMapping("/order")
@Controller("devTestOrderController")
public class OrderController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private BrokerService brokerService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(HttpServletRequest request, Model model) {
		int start = 0;
		if (request.getParameter("start") == null) {
			start = 0;
		} else {
			start = Integer.parseInt(request.getParameter("start"));
			start = start - 1;
		}

		List<OrderModel> orderList = orderService.listByPage("id", "desc", start, 10);
		Long allOrdersCount = orderService.getAllCount();
		int pagesCount = (int) Math.ceil(allOrdersCount / (double) 10);
		int[] pages = new int[pagesCount];
		for (int i = 0; i < pagesCount; i++) {
			pages[i] = 10 * i + 1;
		}

		model.addAttribute("orders", orderList);
		model.addAttribute("pages", pages);
		model.addAttribute("start", start);
		return "order/list";
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.GET)
	public String alacrity(@RequestParam("id") Long orderId, Model model) {
		List<AlacrityModel> listAlacrity = orderService.getAlacrities(orderId);
		List<Broker> brokers = brokerService.getAll();
		if (brokers.size() > 0) {
			model.addAttribute("apiId", brokers.get(0).getApiId());
			model.addAttribute("apiKey", brokers.get(0).getApiKey());
		}

		model.addAttribute("alacrities", listAlacrity);
		model.addAttribute("orderId", orderId);

		return "order/alacrity";
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public String alacrity(HttpServletRequest request) throws IOException, URISyntaxException {

		String url = HttpUtils.getApplicationUrl(request, "/admin").concat("/1.x/carack");
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		OrderModel orderModel = orderService.getOrder(Long.parseLong(request.getParameter("orderId")));

		String params = "orderid=" + orderModel.getUuid() + "&clid=" + request.getParameter("apiId") + "&apikey="
				+ request.getParameter("apiKey") + "&uuid=" + request.getParameter("uuid");

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
	public String setStatus(@RequestParam("id") Long orderId, Model model) {
		Order order = orderService.getTrueOrder(orderId);
		model.addAttribute("apiId", order.getBroker().getApiId());
		model.addAttribute("apiKey", order.getBroker().getApiKey());

		String extra = "";
		if (order.getLastStatus().getStatus() == OrderStatusType.Taked
				|| order.getLastStatus().getStatus() == OrderStatusType.Created) {
			extra = order.getCarUuid();
		}
		model.addAttribute("extra", extra);
		model.addAttribute("orderId", orderId);
		return "order/setStatus";
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
	public String setStatus(HttpServletRequest request) throws IOException, URISyntaxException {

		String clid = request.getParameter("clid");
		String apikey = request.getParameter("apikey");
		Long orderId = Long.parseLong(request.getParameter("id"));
		String status = request.getParameter("status");
		String extra = request.getParameter("extra");
		String newcar = request.getParameter("newcar");

		OrderModel orderModel = orderService.getOrder(orderId);
		String params = "orderid=" + orderModel.getUuid() + "&clid=" + clid + "&apikey=" + apikey;
		if (newcar != null && !newcar.isEmpty()) {
			params += "&newcar=" + newcar;
		} else {
			params += "&status=" + status + "&extra=" + extra;
		}

		String url = HttpUtils.getApplicationUrl(request, "/admin").concat("/1.x/requestconfirm");
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
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

	@RequestMapping(value = "/getGeodata", method = RequestMethod.GET)
	public String getGeodata(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/getGeodata";
	}

	@RequestMapping(value = "/getGeodata", method = RequestMethod.POST)
	public String getGeodata(HttpServletRequest request, @RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId, Model model) {

		OrderModel orderModel = orderService.getOrder(orderId);
		JSONObject getGeodataJson = new JSONObject();

		getGeodataJson.put("apiId", apiId);
		getGeodataJson.put("orderId", orderModel.getUuid());

		try {
			String url = HttpUtils.getApplicationUrl(request, "/admin").concat("/apidevice/order/geodata");
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(getGeodataJson.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				model.addAttribute("result",
						"Error sending order to server: " + IOUtils.toString(connection.getInputStream()));
			} else {
				model.addAttribute("result", IOUtils.toString(connection.getInputStream()));
			}

		} catch (Exception ex) {
			model.addAttribute("result", "Error getting order status: " + ex.toString());
		}

		return "result";
	}

	@RequestMapping(value = "/getStatus", method = RequestMethod.GET)
	public String getStatus(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/getStatus";
	}

	@RequestMapping(value = "/getStatus", method = RequestMethod.POST)
	public String getStatus(HttpServletRequest request, @RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId, Model model) {

		OrderModel orderModel = orderService.getOrder(orderId);
		JSONObject getStatusJson = new JSONObject();

		getStatusJson.put("apiId", apiId);
		getStatusJson.put("orderId", orderModel.getUuid());

		try {
			String url = HttpUtils.getApplicationUrl(request, "/admin").concat("/apidevice/order/status");
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
						"Error sending order to server: " + IOUtils.toString(connection.getInputStream()));
			} else {
				model.addAttribute("result", IOUtils.toString(connection.getInputStream()));
			}

		} catch (Exception ex) {
			model.addAttribute("result", "Error getting order status: " + ex.toString());
		}

		return "result";
	}

	@RequestMapping(value = "/showStatuses", method = RequestMethod.GET)
	public String showStatuses(@RequestParam("id") Long orderId, Model model) {
		List<OrderStatusModel> statusList = orderService.getStatuses(orderId);
		model.addAttribute("statusList", statusList);

		return "order/statusList";
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
			String url = HttpUtils.getApplicationUrl(request, "/admin").concat("/apidevice/order/cancel");
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
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		long t = Calendar.getInstance().getTime().getTime() + (10 * 60000);
		model.addAttribute("now_time", df.format(DatetimeUtils.localTimeToUtc(new Date(t))));
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
		if (formRequirements != null) {
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
		}

		orderJson.put("requirements", requirementsJson);

		String[] brokers = request.getParameterValues("brokers");

		orderJson.put("vehicleClass", request.getParameter("vehicleClass"));
		orderJson.put("brokers", brokers);

		createOrderJson.put("order", orderJson);

		String jsonResult = createOrderJson.toString();

		try {
			String url = HttpUtils.getApplicationUrl(request, "/admin").concat("/apidevice/order/create");
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.write(jsonResult.getBytes("UTF-8"));
			// wr.writeBytes(jsonResult);
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
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));

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
}
