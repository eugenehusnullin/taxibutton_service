package tb2014.admin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IBrokerBusiness;
import tb2014.business.IGeoDataBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.service.order.OrderProcessing;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;

@RequestMapping("/order")
@Controller("devTestOrderController")
public class OrderController {

	private OrderProcessing orderProcessing;
	private IOrderBusiness orderBusiness;
	private IBrokerBusiness brokerBusiness;
	private IOrderStatusBusiness orderStatusBusiness;
	private IOrderAcceptAlacrityBusiness orderAlacrityBusiness;
	private IGeoDataBusiness geoDataBusiness;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness,
			IBrokerBusiness brokerBusiness,
			IOrderStatusBusiness orderStatusBusiness,
			IOrderAcceptAlacrityBusiness orderAlacrityBusiness,
			IGeoDataBusiness geoDataBusiness, OrderProcessing orderProcessing) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
		this.orderStatusBusiness = orderStatusBusiness;
		this.orderProcessing = orderProcessing;
		this.orderAlacrityBusiness = orderAlacrityBusiness;
		this.geoDataBusiness = geoDataBusiness;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("orders", orderBusiness.getAllWithChilds());
		return "order/list";
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.GET)
	public String alacrity(@RequestParam("id") Long orderId, Model model) {

		Order order = orderBusiness.get(orderId);

		model.addAttribute("alacrities", orderAlacrityBusiness.getAll(order));
		model.addAttribute("orderId", orderId);

		return "order/alacrity";
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public String alacrity(HttpServletRequest request) throws IOException {

		String url = "http://localhost:8080/tb2014/apibroker/order/alacrity";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		String params = "orderId=" + request.getParameter("orderId")
				+ "&apiId=" + request.getParameter("apiId") + "&apiKey="
				+ request.getParameter("apiKey") + "&driverName="
				+ request.getParameter("driverName") + "&driverSecondName="
				+ request.getParameter("driverSecondName")
				+ "&driverThirdName=" + request.getParameter("driverThirdName")
				+ "&driverPhone=" + request.getParameter("driverPhone")
				+ "&carNumber=" + request.getParameter("carNumber")
				+ "&carColor=" + request.getParameter("carColor") + "&carMark="
				+ request.getParameter("carMark") + "&carModel="
				+ request.getParameter("carModel");

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(params.getBytes().length));

		connection.setDoOutput(true);

		DataOutputStream outputStream = new DataOutputStream(
				connection.getOutputStream());

		outputStream.writeBytes(params);
		outputStream.flush();
		outputStream.close();

		@SuppressWarnings("unused")
		int responseCode = connection.getResponseCode();

		return "redirect:list";
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.GET)
	public String sendStatus(@RequestParam("id") Long orderId, Model model) {

		Order order = orderBusiness.get(orderId);

		model.addAttribute("orderId", order.getId());
		return "order/setStatus";
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
	public String sendStatus(@RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey,
			@RequestParam("status") String status) throws IOException {

		String url = "http://localhost:8080/tb2014/apibroker/order/setStatus";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		String params = "orderId=" + orderId.toString() + "&apiId=" + apiId
				+ "&apiKey=" + apiKey + "&status=" + status;

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(params.getBytes().length));

		connection.setDoOutput(true);

		DataOutputStream outputStream = new DataOutputStream(
				connection.getOutputStream());

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
	public String setGeoData(@RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey,
			@RequestParam("lon") Double lon, @RequestParam("lat") Double lat,
			@RequestParam("direction") int direction,
			@RequestParam("speed") Double speed,
			@RequestParam("category") String category) throws IOException {

		String url = "http://localhost:8080/tb2014/apibroker/order/setGeoData";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		String params = "orderId=" + orderId.toString() + "&apiId=" + apiId
				+ "&apiKey=" + apiKey + "&lon=" + lon + "&lat=" + lat
				+ "&direction=" + direction + "&speed=" + speed + "&category="
				+ category;

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(params.getBytes().length));

		connection.setDoOutput(true);

		DataOutputStream outputStream = new DataOutputStream(
				connection.getOutputStream());

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
	public String getStatus(@RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey, Model model) {

		Order order = orderBusiness.get(orderId);
		JSONObject getStatusJson = new JSONObject();

		getStatusJson.put("apiId", apiId);
		getStatusJson.put("apiKey", apiKey);
		getStatusJson.put("orderId", order.getUuid());

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/status";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());

			wr.writeBytes(getStatusJson.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error sending order to server");
			}

			model.addAttribute("result",
					getStringFromInputStream(connection.getInputStream()));

		} catch (Exception ex) {
			System.out.println("Error getting order status: " + ex.toString());
		}

		return "order/result";
	}

	@RequestMapping(value = "/getGeoData", method = RequestMethod.GET)
	public String getGeoData(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/getGeoData";
	}

	@RequestMapping(value = "/getGeoData", method = RequestMethod.POST)
	public String getGeoData(@RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey,
			@RequestParam("lastDate") String lastDate, Model model) {

		Order order = orderBusiness.get(orderId);
		JSONObject getGeoDataJson = new JSONObject();

		getGeoDataJson.put("apiId", apiId);
		getGeoDataJson.put("apiKey", apiKey);
		getGeoDataJson.put("orderId", order.getUuid());
		getGeoDataJson.put("lastDate", lastDate);

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/geodata";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());

			wr.writeBytes(getGeoDataJson.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error sending order to server");
			}

			model.addAttribute("result",
					getStringFromInputStream(connection.getInputStream()));

		} catch (Exception ex) {
			System.out.println("Error getting order status: " + ex.toString());
		}

		return "order/result";
	}

	@RequestMapping(value = "/showStatus", method = RequestMethod.GET)
	public String showStatus(@RequestParam("id") Long orderId, Model model) {

		Order order = orderBusiness.get(orderId);
		List<OrderStatus> statusList = orderStatusBusiness.get(order);

		model.addAttribute("statusList", statusList);

		return "order/statusList";
	}

	@RequestMapping(value = "/showGeoData", method = RequestMethod.GET)
	public String showGeoData(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("geoList",
				geoDataBusiness.getAll(orderBusiness.get(orderId)));
		return "order/geoList";
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public String cancel(@RequestParam("id") Long orderId) {

		String reason = "temp reason of cancelling";
		Order order = orderBusiness.getWithChilds(orderId);

		orderProcessing.cancelOrder(order, reason);

		return "redirect:list";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "order/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(HttpServletRequest request) {

		JSONObject createOrderJson = new JSONObject();

		createOrderJson.put("apiId", request.getParameter("apiId"));
		createOrderJson.put("apiKey", request.getParameter("apiKey"));

		JSONObject orderJson = new JSONObject();

		orderJson.put("recipientBlackListed", "no");
		orderJson.put("recipientLoyal", "yes");
		orderJson.put("recipientPhone", request.getParameter("phone"));

		JSONObject sourceJson = new JSONObject();

		sourceJson.put("lon", request.getParameter("sourceLon"));
		sourceJson.put("lat", request.getParameter("sourceLat"));
		sourceJson.put("fullAddress", request.getParameter("sFullAddress"));
		sourceJson.put("shortAddress", request.getParameter("sShortAddress"));
		sourceJson.put("closestStation",
				request.getParameter("sClosestStation"));
		sourceJson.put("country", request.getParameter("sCountry"));
		sourceJson.put("locality", request.getParameter("sLocality"));
		sourceJson.put("street", request.getParameter("sStreet"));
		sourceJson.put("housing", request.getParameter("sHousing"));

		orderJson.put("source", sourceJson);

		JSONArray destinationsJson = new JSONArray();
		JSONObject destinationJson = new JSONObject();

		destinationJson.put("index", "1");
		destinationJson.put("lon", request.getParameter("destinationLon"));
		destinationJson.put("lat", request.getParameter("destinationLat"));
		destinationJson
				.put("fullAddress", request.getParameter("dFullAddress"));
		destinationJson.put("shortAddress",
				request.getParameter("dShortAddress"));
		destinationJson.put("closestStation",
				request.getParameter("dClosestStation"));
		destinationJson.put("country", request.getParameter("dCountry"));
		destinationJson.put("locality", request.getParameter("dLocality"));
		destinationJson.put("street", request.getParameter("dStreet"));
		destinationJson.put("housing", request.getParameter("dHousing"));

		destinationsJson.put(destinationJson);

		orderJson.put("destinations", destinationsJson);
		orderJson.put("bookingType", request.getParameter("orderType"));
		orderJson.put("bookingDate", request.getParameter("bookingDate"));
		orderJson.put("bookingHour", request.getParameter("bookingHour"));
		orderJson.put("bookingMin", request.getParameter("bookingMin"));

		JSONArray requirementsJson = new JSONArray();

		String[] formRequirements = request.getParameterValues("requirements");

		for (String currentRequirementName : formRequirements) {

			JSONObject currentRequirementJson = new JSONObject();

			currentRequirementJson.put("name", currentRequirementName);

			if (currentRequirementName.trim().equals("isChildChair")) {
				currentRequirementJson.put("value",
						request.getParameter("childAge"));
			} else {
				currentRequirementJson.put("value", "yes");
			}

			requirementsJson.put(currentRequirementJson);
		}

		orderJson.put("requirements", requirementsJson);

		createOrderJson.put("order", orderJson);

		String jsonResult = createOrderJson.toString();

		try {

			String url = "http://localhost:8080/tb2014/apidevice/order/create";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());

			wr.writeBytes(jsonResult);
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error sending order to server");
			}

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
				System.out.println("Error receiving server respone: "
						+ ex.toString());
			}

			JSONObject responseJson = (JSONObject) new JSONTokener(
					stringBuffer.toString()).nextValue();

			System.out.println("Server JSON response: "
					+ responseJson.toString());
		} catch (Exception ex) {
			System.out.println("Error creating new order: " + ex.toString());
		}

		return "redirect:list";
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public String send(@RequestParam("id") Long orderId, Model model) {

		orderProcessing.offerOrder(orderId);

		return "redirect:list";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(@RequestParam("id") Long orderId) {

		orderProcessing.deleteOrder(orderId);

		return "redirect:list";
	}

	@RequestMapping(value = "/give", method = RequestMethod.GET)
	public String give(@RequestParam("id") Long orderId, Model model) {

		model.addAttribute("orderId", orderId);
		return "order/give";
	}

	@RequestMapping(value = "/give", method = RequestMethod.POST)
	public String give(@RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId) {

		Broker broker = brokerBusiness.getByApiId(apiId);

		orderProcessing.giveOrder(orderId, broker);

		return "redirect:list";
	}

	@RequestMapping(value = "/offer", method = RequestMethod.POST)
	public void offer(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		DataInputStream inputStream = new DataInputStream(
				request.getInputStream());

		try {
			Source source = new StreamSource(inputStream);
			Result outputTarget = new StreamResult(System.out);

			TransformerFactory.newInstance().newTransformer()
					.transform(source, outputTarget);
		} catch (Exception ex) {
			System.out
					.println("Error recieving XML document: " + ex.toString());
		}

		response.setContentType("text/html");
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
	}

	private String getStringFromInputStream(InputStream stream) {

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(stream));

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception ex) {
			System.out.println("Error greating string from input stream: "
					+ ex.toString());
		}

		return stringBuffer.toString();
	}
}
