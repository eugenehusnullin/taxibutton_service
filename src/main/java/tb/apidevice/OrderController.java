package tb.apidevice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

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
import org.w3c.dom.Document;

import tb.domain.Broker;
import tb.domain.order.Order;
import tb.service.BrokerService;
import tb.service.OfferingOrder;
import tb.service.OrderService;
import tb.service.exceptions.DeviceNotFoundException;
import tb.service.exceptions.NotValidOrderStatusException;
import tb.service.exceptions.OrderNotFoundException;
import tb.service.exceptions.ParseOrderException;
import tb.service.exceptions.WrongData;
import tb.utils.HttpUtils;
import tb.utils.NetStreamUtils;

@RequestMapping("/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private OfferingOrder offeringOrder;

	// create an order from apk request (json string)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void create(HttpServletRequest request, HttpServletResponse response) {

		try {
			String str = getHttpServletRequestBuffer(request);
			log.trace(str);

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
				log.error(dnfe.toString());
			} catch (ParseOrderException e) {
				response.sendError(404, e.toString());
				log.error(e.toString());
			}
		} catch (UnsupportedEncodingException e) {
			log.error("apiDeviceOrderController.create", e);
			response.setStatus(500);
		} catch (IOException e) {
			log.error("apiDeviceOrderController.create", e);
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
			log.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		} catch (IOException e) {
			log.error("apiDeviceOrderController.cancel", e);
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
			log.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		} catch (IOException e) {
			log.error("apiDeviceOrderController.cancel", e);
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
			log.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		} catch (IOException e) {
			log.error("apiDeviceOrderController.cancel", e);
			response.setStatus(500);
		}
	}

	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public void feedback(HttpServletRequest request, HttpServletResponse response) {
		try {
			String str = getHttpServletRequestBuffer(request);
			log.trace(str);

			JSONObject feedbackJson = (JSONObject) new JSONTokener(str).nextValue();
			orderService.saveFeedback(feedbackJson);
			response.setStatus(200);
		} catch (UnsupportedEncodingException e) {
			log.error("apiDeviceOrderController.feedback", e);
			response.setStatus(500);
		} catch (IOException e) {
			log.error("apiDeviceOrderController.feedback", e);
			response.setStatus(500);
		} catch (OrderNotFoundException e) {
			log.error("apiDeviceOrderController.feedback", e);
			response.setStatus(403);
		} catch (WrongData e) {
			log.error("apiDeviceOrderController.feedback", e);
			response.setStatus(404);
		}
	}

	@RequestMapping(value = "/getbrokers", method = RequestMethod.POST)
	@Transactional
	public void getBrokers(HttpServletRequest request, HttpServletResponse response) {
		try {

			StringBuffer stringBuffer = NetStreamUtils.getHttpServletRequestBuffer(request);
			JSONObject requestJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();

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

	@RequestMapping(value = "/cost", method = RequestMethod.POST)
	public void cost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String str = getHttpServletRequestBuffer(request);
			JSONObject costOrderObject = (JSONObject) new JSONTokener(str).nextValue();
			
			Order order = orderService.initOrder(costOrderObject);
			String uuid = costOrderObject.optString("uuid");
			Broker broker = brokerService.getByUuid(uuid);
			Document doc = offeringOrder.createExactOffer(order, broker);
			HttpURLConnection connection = HttpUtils.postDocumentOverHttp(doc, broker.getCostUrl());
			IOUtils.copy(connection.getInputStream(), response.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeviceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseOrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getHttpServletRequestBuffer(HttpServletRequest request) throws IOException {
		return IOUtils.toString(request.getInputStream(), "UTF-8");
	}
}
