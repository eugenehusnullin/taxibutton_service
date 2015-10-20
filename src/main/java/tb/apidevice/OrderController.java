package tb.apidevice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb.domain.Broker;
import tb.domain.maparea.Point;
import tb.domain.order.Order;
import tb.domain.order.VehicleClass;
import tb.service.BrokerService;
import tb.service.OrderService;
import tb.service.exceptions.DeviceNotFoundException;
import tb.service.exceptions.NotValidOrderStatusException;
import tb.service.exceptions.OrderNotFoundException;
import tb.service.exceptions.ParseOrderException;
import tb.service.exceptions.WrongData;
import tb.tariffdefinition.CostRequest;

@RequestMapping("/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;
	@Autowired
	private BrokerService brokerService;
	// @Autowired
	// private OfferingOrderYandexTaxi offeringOrder;

	// create an order from apk request (json string)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void create(HttpServletRequest request, HttpServletResponse response) {

		try {
			String str = getHttpServletRequestBuffer(request);
			logger.trace(str);

			JSONObject createOrderObject = (JSONObject) new JSONTokener(str).nextValue();

			try {
				Order order = orderService.initOrder(createOrderObject);
				orderService.create(order);
				String orderUuid = order.getUuid();
				JSONObject responseJson = new JSONObject();
				responseJson.put("status", "ok");
				responseJson.put("orderId", orderUuid);
				response.setStatus(200);
				IOUtils.write(responseJson.toString(), response.getOutputStream(), "UTF-8");
			} catch (DeviceNotFoundException dnfe) {
				response.setStatus(403);
				logger.error(dnfe.toString());
			} catch (ParseOrderException e) {
				response.sendError(404, e.toString());
				logger.error(e.toString());
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("apiDeviceOrderController.create", e);
			response.setStatus(500);
		} catch (IOException e) {
			logger.error("apiDeviceOrderController.create", e);
			response.setStatus(500);
		}
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public void cancel(HttpServletRequest request, HttpServletResponse response) {

		try {
			String str = getHttpServletRequestBuffer(request);
			JSONObject cancelOrderJson = (JSONObject) new JSONTokener(str).nextValue();
			try {
				orderService.cancel(cancelOrderJson);

				JSONObject responseJson = new JSONObject();
				response.setStatus(200);
				responseJson.put("status", "success");
				IOUtils.write(responseJson.toString(), response.getOutputStream(), "UTF-8");

			} catch (DeviceNotFoundException e) {
				response.setStatus(403);
			} catch (OrderNotFoundException e) {
				response.setStatus(404);
			} catch (NotValidOrderStatusException e) {
				response.setStatus(404);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		} catch (IOException e) {
			logger.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		}
	}

	// get status of order
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public void status(HttpServletRequest request, HttpServletResponse response) {

		try {
			String str = getHttpServletRequestBuffer(request);
			JSONObject getStatusObject = (JSONObject) new JSONTokener(str).nextValue();

			try {
				JSONObject statusJson = orderService.getStatus(getStatusObject);
				IOUtils.write(statusJson.toString(), response.getOutputStream(), "UTF-8");

			} catch (DeviceNotFoundException e) {
				response.setStatus(403);
			} catch (OrderNotFoundException e) {
				response.setStatus(403);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		} catch (IOException e) {
			logger.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		}
	}

	// get order geo data
	@RequestMapping(value = "/geodata", method = RequestMethod.POST)
	public void geodata(HttpServletRequest request, HttpServletResponse response) {

		try {
			String str = getHttpServletRequestBuffer(request);
			JSONObject jsonObject = (JSONObject) new JSONTokener(str).nextValue();

			try {
				JSONObject geoDataJson = orderService.getGeodata(jsonObject);
				IOUtils.write(geoDataJson.toString(), response.getOutputStream(), "UTF-8");
			} catch (DeviceNotFoundException e1) {
				response.setStatus(403);
			} catch (OrderNotFoundException e1) {
				response.setStatus(403);
			} catch (ParseException e1) {
				response.setStatus(403);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		} catch (IOException e) {
			logger.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		}
	}

	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public void feedback(HttpServletRequest request, HttpServletResponse response) {
		try {
			String str = getHttpServletRequestBuffer(request);
			logger.trace(str);

			JSONObject feedbackJson = (JSONObject) new JSONTokener(str).nextValue();
			orderService.saveFeedback(feedbackJson);
			response.setStatus(200);
		} catch (UnsupportedEncodingException e) {
			logger.error("apiDeviceOrderController.feedback", e);
			response.setStatus(500);
		} catch (IOException e) {
			logger.error("apiDeviceOrderController.feedback", e);
			response.setStatus(500);
		} catch (OrderNotFoundException e) {
			logger.error("apiDeviceOrderController.feedback", e);
			response.setStatus(403);
		} catch (WrongData e) {
			logger.error("apiDeviceOrderController.feedback", e);
			response.setStatus(404);
		}
	}
	
	@RequestMapping(value = "/inform", method = RequestMethod.POST)
	public void inform(HttpServletRequest request, HttpServletResponse response) {
		try {
			String str = getHttpServletRequestBuffer(request);
			logger.trace(str);

			JSONObject informJson = (JSONObject) new JSONTokener(str).nextValue();
			orderService.informEvent(informJson);
			response.setStatus(200);
		} catch (UnsupportedEncodingException e) {
			logger.error("apiDeviceOrderController.gotocar", e);
			response.setStatus(500);
		} catch (IOException e) {
			logger.error("apiDeviceOrderController.gotocar", e);
			response.setStatus(500);
		} catch (OrderNotFoundException e) {
			logger.error("apiDeviceOrderController.gotocar", e);
			response.setStatus(403);
		} catch (WrongData e) {
			logger.error("apiDeviceOrderController.gotocar", e);
			response.setStatus(404);
		}
	}

	@RequestMapping(value = "/getbrokers", method = RequestMethod.POST)
	@Transactional
	public void getBrokers(HttpServletRequest request, HttpServletResponse response) {
		try {

			JSONObject requestJson = (JSONObject) new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")).nextValue();

			String lat = requestJson.optString("lat");
			String lon = requestJson.optString("lon");
			List<Broker> brokers = null;
			if (lat != null && lon != null) {
				brokers = brokerService.getBrokersByMapAreas(Double.parseDouble(lat), Double.parseDouble(lon));
			} else {
				brokers = brokerService.getAll();
			}

			JSONArray jsonArray = new JSONArray();
			for (Broker broker : brokers) {
				JSONObject jsonBroker = new JSONObject();
				jsonBroker.put("uuid", broker.getUuid());
				jsonBroker.put("name", broker.getName());
				jsonArray.put(jsonBroker);
			}
			IOUtils.write(jsonArray.toString(), response.getOutputStream(), "UTF-8");
		} catch (Exception e) {

		}
	}

	@Autowired
	private CostRequest costRequest;

	@RequestMapping(value = "/cost", method = RequestMethod.POST)
	public void cost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String str = getHttpServletRequestBuffer(request);
			JSONObject costJson = (JSONObject) new JSONTokener(str).nextValue();

			Point source = createPoint(costJson.getJSONObject("source"));
			List<Point> destinations = createPoints(costJson.optJSONArray("destinations"));
			Date bookDate = createBookDate(costJson.getString("bookingDate"));
			VehicleClass vehicleClass = createVehicleClass(costJson.getInt("class"));
			List<String> adds = createAdds(costJson.optJSONArray("adds"));

			JSONObject resultJson = costRequest.getCost(source, destinations, vehicleClass, bookDate, adds);
			IOUtils.write(resultJson.toString(), response.getOutputStream(), "UTF-8");
		} catch (Exception e) {
			logger.error("cost", e);
		}
	}

	private List<String> createAdds(JSONArray addsJson) {
		if (addsJson == null) {
			return null;
		} else {
			List<String> adds = new ArrayList<String>();
			for (int i = 0; i < addsJson.length(); i++) {
				adds.add(addsJson.getString(i));
			}
			return adds;
		}
	}

	private VehicleClass createVehicleClass(int value) {
		return VehicleClass.values()[value];
	}

	private Date createBookDate(String bookingDateStr) {
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			return dateFormatter.parse(bookingDateStr);
		} catch (Exception ex) {
			return null;
		}
	}

	private List<Point> createPoints(JSONArray pointsJson) {
		if (pointsJson == null) {
			return null;
		} else {
			List<Point> points = new ArrayList<Point>();
			for (int i = 0; i < pointsJson.length(); i++) {
				Point point = createPoint(pointsJson.getJSONObject(i));
				points.add(point);
			}
			return points;
		}
	}

	private Point createPoint(JSONObject pointJson) {
		Point point = new Point();
		point.setLatitude(pointJson.optDouble("lat"));
		point.setLongitude(pointJson.optDouble("lon"));
		return point;
	}

	private String getHttpServletRequestBuffer(HttpServletRequest request) throws IOException {
		return IOUtils.toString(request.getInputStream(), "UTF-8");
	}
}
