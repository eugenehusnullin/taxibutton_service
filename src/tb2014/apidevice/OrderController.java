package tb2014.apidevice;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.lib.DataOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.business.IBrokerBusiness;
import tb2014.business.IDeviceBusiness;
import tb2014.business.IGeoDataBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.domain.Device;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.service.order.CancelOrderProcessing;
import tb2014.service.order.OfferOrderProcessing;
import tb2014.service.order.OrderProcessing;
import tb2014.service.serialize.OrderJsonParser;

@RequestMapping("/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private IOrderBusiness orderBusiness;
	@Autowired
	private IBrokerBusiness brokerBusiness;
	@Autowired
	private IOrderStatusBusiness orderStatusBusiness;
	@Autowired
	private IOrderAcceptAlacrityBusiness orderAcceptAlacrityBusiness;
	@Autowired
	private IGeoDataBusiness geoDataBusiness;	
	@Autowired
	private OrderProcessing orderProcessing;
	@Autowired
	private OfferOrderProcessing offerOrderProcessing;
	@Autowired
	private CancelOrderProcessing cancelorderProcessing;
	@Autowired
	private IDeviceBusiness deviceBusiness;

	// create an order from apk request (json string)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Transactional
	public void create(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {

			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			log.trace(stringBuffer.toString());

			JSONObject createOrderObject = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
			System.out.println("Json request: " + createOrderObject.toString());

			String apiId = null;

			if (!createOrderObject.isNull("apiId")) {
				apiId = createOrderObject.getString("apiId");
			} else {
				response.setStatus(403);
				return;
			}

			Device device = deviceBusiness.get(apiId);
			if (device != null) {

				JSONObject orderObject = createOrderObject.getJSONObject("order");
				JSONObject responseJson = new JSONObject();
				Order order = OrderJsonParser.Json2Order(orderObject, brokerBusiness);

				if (order == null) {

					responseJson.put("status", "error");
					responseJson.put("orderId", "null");
					response.setStatus(403);
				} else {

					order.setDevice(device);
					order.setUuid(UUID.randomUUID().toString());
					orderBusiness.saveNewOrder(order);

					// create new order status (Created)
					OrderStatus orderStatus = new OrderStatus();
					orderStatus.setDate(new Date());
					orderStatus.setOrder(order);
					orderStatus.setStatus(OrderStatusType.Created);
					orderStatusBusiness.save(orderStatus);

					responseJson.put("status", "ok");
					responseJson.put("orderId", order.getUuid().toString());
					response.setStatus(200);

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MINUTE, 1);
					order.setStartOffer(cal.getTime());
					offerOrderProcessing.addOrder(order);
				}

				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
				outputStream.writeBytes(responseJson.toString());
				outputStream.flush();
				outputStream.close();
			} else {
				response.setStatus(403);
			}
		} catch (Exception ex) {
			log.warn(ex.toString());
			throw ex;
		}
	}

	// cancel order
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public void cancel(HttpServletRequest request, HttpServletResponse response) {

		try {

			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject cancelOrderJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
			JSONObject responseJson = new JSONObject();
			int statusCode = 0;
			String apiId = null;

			try {
				apiId = cancelOrderJson.getString("apiId");
			} catch (JSONException ex) {
				statusCode = 403;
				response.setStatus(statusCode);
				return;
			}

			String orderUuid = null;
			String reason = null;

			try {

				orderUuid = cancelOrderJson.getString("orderId");
				reason = cancelOrderJson.getString("reason");
			} catch (JSONException ex) {
				statusCode = 403;
				response.setStatus(statusCode);
				return;
			}

			Order order = orderBusiness.getWithChilds(orderUuid);

			if (order == null) {
				statusCode = 404;
			} else {
				statusCode = cancelAction(order, reason, apiId);
			}

			response.setStatus(statusCode);

			if (statusCode != 200) {
				responseJson.put("status", "error");
			} else {
				responseJson.put("status", "success");
			}

			responseJson.put("orderId", order.getUuid().toString());

			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			System.out.println("Error parsing JSON to object: " + ex.toString());
			response.setStatus(500);
			return;
		}
	}

	private int cancelAction(Order order, String reason, String apiId) {

		int resultCode = 0;
		Device device = deviceBusiness.get(apiId);
		if (device != null) {

			if (order == null) {
				resultCode = 404;
				return resultCode;
			}

			if (!order.getDevice().getApiId().equals(apiId)) {
				resultCode = 403;
				return resultCode;
			}

			OrderStatus status = orderStatusBusiness.getLastWithChilds(order);

			if (status == null) {
				resultCode = 404;
				return resultCode;
			}

			if (status.getStatus() == OrderStatusType.Created || status.getStatus() == OrderStatusType.Taked || status.getStatus() == OrderStatusType.Prepared) {

				if (orderProcessing.cancelOrder(order, reason)) {
					resultCode = 200;
					return resultCode;
				} else {
					resultCode = 500;
					return resultCode;
				}
			} else {
				resultCode = 500;
				return resultCode;
			}
		} else {
			resultCode = 403;
			return resultCode;
		}
	}

	// get status of order
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public void status(HttpServletRequest request, HttpServletResponse response) {

		try {

			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject getStatusObject = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
			int statusCode = 0;
			String apiId = null;

			try {
				apiId = getStatusObject.getString("apiId");
			} catch (JSONException ex) {
				statusCode = 403;
				response.setStatus(statusCode);
				return;
			}

			Device device = deviceBusiness.get(apiId);
			if (device != null) {

				String orderUuid = null;

				try {
					orderUuid = getStatusObject.getString("orderId");
				} catch (JSONException ex) {
					statusCode = 403;
					response.setStatus(statusCode);
					return;
				}

				Order order = orderBusiness.getWithChilds(orderUuid);

				if (order == null) {
					response.setStatus(404);
					return;
				}

				OrderStatus status = orderStatusBusiness.getLastWithChilds(order);

				if (status != null) {

					JSONObject statusJson = new JSONObject();

					statusJson.put("orderId", order.getUuid().toString());

					// isn't executing by any broker
					if (order.getBroker() == null) {

						// no alacrities
						if (orderAcceptAlacrityBusiness.getAll(order).size() == 0) {
							statusJson.put("status", status.getStatus().toString());
						} else {// there are any alacrities
							statusJson.put("status", "Prepared");
						}

						statusJson.put("date", new Date());
					} else {// is executing by broker

						statusJson.put("status", status.getStatus().toString());
						statusJson.put("executor", order.getBroker().getName());
						statusJson.put("date", status.getDate());
					}

					DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
					byte[] bytes = statusJson.toString().getBytes("UTF-8");

					outputStream.write(bytes);
					outputStream.flush();
					outputStream.close();
				} else {
					response.setStatus(404);
					return;
				}
			} else {
				response.setStatus(403);
			}
		} catch (Exception ex) {
			System.out.println("Error parsing JSON to object: " + ex.toString());
		}
	}

	// get order geo data
	@RequestMapping(value = "/geodata", method = RequestMethod.POST)
	public void geodata(HttpServletRequest request, HttpServletResponse response) {

		try {
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject jsonObject = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
			int statusCode = 0;
			String apiId = null;

			try {
				apiId = jsonObject.getString("apiId");
			} catch (JSONException ex) {
				statusCode = 403;
				response.setStatus(statusCode);
				return;
			}

			Device device = deviceBusiness.get(apiId);
			if (device != null) {

				String orderUuid = null;

				try {
					orderUuid = jsonObject.getString("orderId");
				} catch (JSONException ex) {
					statusCode = 403;
					response.setStatus(statusCode);
					return;
				}

				Order order = orderBusiness.get(orderUuid);

				if (order == null) {
					response.setStatus(404);
					return;
				}

				List<GeoData> geoDataList = null;
				String lastDate = null;

				try {
					lastDate = jsonObject.getString("lastDate");
				} catch (JSONException ex) {
					statusCode = 403;
					response.setStatus(statusCode);
					return;
				}

				// first request for order geo data
				if (lastDate.isEmpty()) {
					geoDataList = geoDataBusiness.getAll(order);
				} else {

					Date date = null;

					try {
						date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(jsonObject.getString("lastDate"));
					} catch (JSONException e) {
						System.out.println(e.toString());
					} catch (ParseException e) {
						System.out.println(e.toString());
					}

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					System.out.println(dateFormat.format(date));

					geoDataList = geoDataBusiness.getAll(order, date);
				}

				JSONObject geoDataJson = new JSONObject();
				JSONArray geoPointsArrayJson = new JSONArray();

				geoDataJson.put("orderId", order.getUuid());

				for (GeoData currentPoint : geoDataList) {

					JSONObject currentPointJson = new JSONObject();

					currentPointJson.put("lat", currentPoint.getLat());
					currentPointJson.put("lon", currentPoint.getLon());
					currentPointJson.put("direction", currentPoint.getDirection());
					currentPointJson.put("speed", currentPoint.getSpeed());
					currentPointJson.put("category", currentPoint.getCategory());
					currentPointJson.put("date", currentPoint.getDate());

					geoPointsArrayJson.put(currentPointJson);
				}

				geoDataJson.put("points", geoPointsArrayJson);

				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());

				outputStream.writeBytes(geoDataJson.toString());
				outputStream.flush();
				outputStream.close();
			} else {

				response.setStatus(403);
			}
		} catch (Exception ex) {
			System.out.println("Error parsing JSON to object: " + ex.toString());
		}
	}

	private StringBuffer getHttpServletRequestBuffer(HttpServletRequest request) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {
			BufferedReader bufferedReader = request.getReader();

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}

			return stringBuffer;
		} catch (Exception ex) {
			log.error("Error creating string buffer from HttpServletRequest.", ex);
			throw ex;
		}
	}
}
