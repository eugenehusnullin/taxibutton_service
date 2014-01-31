package tb2014.dev.mvc.controllers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IBrokerBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.service.order.OrdersProcessing;
import tb2014.domain.Broker;
import tb2014.domain.order.AddressPoint;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.Requirement;

@RequestMapping("/order")
@Controller
public class OrderController {

	private IOrderBusiness orderBusiness;
	private IBrokerBusiness brokerBusiness;
	private IOrderStatusBusiness orderStatusBusiness;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness,
			IBrokerBusiness brokerBusiness,
			IOrderStatusBusiness orderStatusBusiness) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
		this.orderStatusBusiness = orderStatusBusiness;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		List<Order> orders = orderBusiness.getAll();

		for (Order currentOrder : orders) {
			System.out.println("Current order: " + currentOrder.getId() + "---"
					+ currentOrder.getType());
		}

		model.addAttribute("orders", orderBusiness.getAll());
		return "order/list";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "order/create";
	}

	@RequestMapping(value = "/sendStatus", method = RequestMethod.GET)
	public String sendStatus(@RequestParam("id") Long orderId, Model model) {

		Order order = orderBusiness.get(orderId);

		model.addAttribute("orderId", order.getId());
		return "order/sendStatus";
	}

	@RequestMapping(value = "/sendStatus", method = RequestMethod.POST)
	public String sendStatus(@RequestParam("orderId") Long orderId,
			@RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey,
			@RequestParam("status") String status,
			@RequestParam("latitude") double latitude,
			@RequestParam("longitude") double longitude,
			@RequestParam("direction") int direction,
			@RequestParam("speed") int speed,
			@RequestParam("category") String category) throws IOException {

		String url = "http://localhost:8080/tb2014/apiOrder/setStatus";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		String params = "orderId=" + orderId.toString() + "&apiId=" + apiId
				+ "&apiKey=" + apiKey + "&status=" + status + "&latitude="
				+ latitude + "&longitude=" + longitude + "&direction="
				+ direction + "&speed=" + speed + "&category=" + category;

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

		//int responseCode = connection.getResponseCode();

		return "redirect:list";
	}

	@RequestMapping(value = "/showStatus", method = RequestMethod.GET)
	public String showStatus(@RequestParam("id") Long orderId, Model model) {

		Order order = orderBusiness.get(orderId);
		List<OrderStatus> statusList = orderStatusBusiness.get(order);
		
		model.addAttribute("statusList", statusList);
		
		return "order/statusList";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(HttpServletRequest request) {

		Order order = new Order();

		order.setType(request.getParameter("orderType"));
		order.setPhone(request.getParameter("phone"));

		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

		if (request.getParameter("bookingDate").isEmpty() == false) {
			String orderDate = request.getParameter("bookingDate");
			Date resultDate = null;

			try {
				resultDate = dateFormatter.parse(orderDate);
				order.setSupplyDate(resultDate);
			} catch (Exception ex) {
				System.out.println("");
			}
		}

		if (request.getParameter("bookingHour").isEmpty() == false) {
			order.setSupplyHour(Integer.parseInt(request
					.getParameter("bookingHour")));
		}

		if (request.getParameter("bookingMin").isEmpty() == false) {
			order.setSupplyMin(Integer.parseInt(request
					.getParameter("bookingMin")));
		}

		List<AddressPoint> addressPoints = new ArrayList<AddressPoint>();

		// source point
		AddressPoint source = new AddressPoint();

		source.setType(Byte.parseByte("0"));

		if (request.getParameter("sourceLon").isEmpty() == false) {
			source.setLon(Double.parseDouble(request.getParameter("sourceLon")));
		}

		if (request.getParameter("sourceLat").isEmpty() == false) {
			source.setLat(Double.parseDouble(request.getParameter("sourceLat")));
		}

		source.setFullAddress(request.getParameter("sFullAddress"));
		source.setShortAddress(request.getParameter("sShortAddress"));
		source.setClosesStation(request.getParameter("sClosestStation"));
		source.setCounty(request.getParameter("sCountry"));
		source.setLocality(request.getParameter("sLocality"));
		source.setStreet(request.getParameter("sStreet"));
		source.setHousing(request.getParameter("sHousing"));
		source.setOrder(order);

		addressPoints.add(source);

		AddressPoint destination = new AddressPoint();

		destination.setType(Byte.parseByte("1"));

		if (request.getParameter("destinationLon").isEmpty() == false) {
			destination.setLon(Double.parseDouble(request
					.getParameter("destinationLon")));
		}

		if (request.getParameter("destinationLat").isEmpty() == false) {
			destination.setLat(Double.parseDouble(request
					.getParameter("destinationLat")));
		}

		destination.setFullAddress(request.getParameter("dFullAddress"));
		destination.setShortAddress(request.getParameter("dShortAddress"));
		destination.setClosesStation(request.getParameter("dClosestStation"));
		destination.setCounty(request.getParameter("dCountry"));
		destination.setLocality(request.getParameter("dLocality"));
		destination.setStreet(request.getParameter("dStreet"));
		destination.setHousing(request.getParameter("dHousing"));
		destination.setOrder(order);

		addressPoints.add(destination);

		order.setDestinations(addressPoints);

		Set<Requirement> requirements = new HashSet<Requirement>();

		Requirement currentRequirement = new Requirement();

		String[] formRequirements = request.getParameterValues("requirements");

		for (String currentRequirementName : formRequirements) {
			currentRequirement = new Requirement();
			currentRequirement.setType(currentRequirementName);

			if (currentRequirementName.trim().equals("isChildChair")) {
				currentRequirement.setOptions(request.getParameter("childAge"));
			}

			currentRequirement.setOrder(order);
			requirements.add(currentRequirement);
		}

		order.setRequirements(requirements);
		orderBusiness.save(order);

		return "redirect:list";
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public String send(@RequestParam("id") Long orderId, Model model) {

		OrdersProcessing orderProcessing = new OrdersProcessing(orderBusiness,
				brokerBusiness);

		orderProcessing.OfferOrder(orderId);

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

		OrdersProcessing orderProcessing = new OrdersProcessing(orderBusiness,
				brokerBusiness);

		Broker broker = brokerBusiness.getByApiId(apiId);

		orderProcessing.GiveOrder(orderId, broker);

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
}
