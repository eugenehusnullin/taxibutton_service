package tb.apidevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.lib.DataOutputStream;
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
import tb.service.BrokerService;
import tb.service.OrderService;
import tb.service.exceptions.DeviceNotFoundException;
import tb.service.exceptions.WrongData;
import tb.service.exceptions.NotValidOrderStatusException;
import tb.service.exceptions.OrderNotFoundException;
import tb.service.exceptions.ParseOrderException;

@RequestMapping("/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;
	@Autowired
	private BrokerService brokerService;

	// create an order from apk request (json string)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void create(HttpServletRequest request, HttpServletResponse response) {

		try {
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			log.trace(stringBuffer.toString());

			JSONObject createOrderObject = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();

			try {
				String orderUuid = orderService.createFromDevice(createOrderObject);

				JSONObject responseJson = new JSONObject();
				responseJson.put("status", "ok");
				responseJson.put("orderId", orderUuid);
				response.setStatus(200);

				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
				outputStream.writeBytes(responseJson.toString());
				outputStream.flush();
				outputStream.close();

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
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject cancelOrderJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
			try {
				orderService.cancel(cancelOrderJson);

				JSONObject responseJson = new JSONObject();
				response.setStatus(200);
				responseJson.put("status", "success");

				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
				outputStream.writeBytes(responseJson.toString());
				outputStream.flush();
				outputStream.close();

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
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject getStatusObject = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();

			try {
				JSONObject statusJson = orderService.getStatus(getStatusObject);

				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
				byte[] bytes = statusJson.toString().getBytes("UTF-8");
				outputStream.write(bytes);
				outputStream.flush();
				outputStream.close();

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
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject jsonObject = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();

			try {
				JSONObject geoDataJson = orderService.getGeodata(jsonObject);
				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());

				outputStream.writeBytes(geoDataJson.toString());
				outputStream.flush();
				outputStream.close();
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
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			log.trace(stringBuffer.toString());

			JSONObject feedbackJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
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
	
	@RequestMapping(value = "/getbrokers", method = RequestMethod.GET)
	@Transactional
	public void getBrokers(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Broker> brokers = brokerService.getAll();
			JSONArray jsonArray = new JSONArray();
			for (Broker broker : brokers) {
				JSONObject jsonBroker = new JSONObject();
				jsonBroker.put("uuid", broker.getUuid());
				jsonBroker.put("name", broker.getName());
				jsonArray.put(jsonBroker);
				//jsonArray.put(broker.getUuid());
				//jsonArray.put(broker.getName());
			}
			
			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(outputStream);
			jsonArray.write(osw);
			osw.flush();
			osw.close();
		} catch (Exception e) {

		}
	}

	private StringBuffer getHttpServletRequestBuffer(HttpServletRequest request) throws UnsupportedEncodingException,
			IOException {
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

		while ((line = bufferedReader.readLine()) != null) {
			stringBuffer.append(line);
		}

		return stringBuffer;
	}
}