package tb2014.apidevice;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.business.IGeoDataBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.OrderStatusType;
import tb2014.service.serialize.OrderJsonParser;
import tb2014.utils.DeviceUtil;

@RequestMapping("/apidevice/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private IOrderBusiness orderBusiness;
	private IOrderStatusBusiness orderStatusBusiness;
	private IOrderAcceptAlacrityBusiness orderAcceptAlacrityBusiness;
	private IGeoDataBusiness geoDataBusiness;
	private DeviceUtil deviceUtil;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness, DeviceUtil deviceUtil,
			IOrderStatusBusiness orderStatusBusines,
			IOrderAcceptAlacrityBusiness orderAcceptAlacrityBusiness,
			IGeoDataBusiness geoDataBusiness) {
		this.orderBusiness = orderBusiness;
		this.deviceUtil = deviceUtil;
		this.orderStatusBusiness = orderStatusBusines;
		this.orderAcceptAlacrityBusiness = orderAcceptAlacrityBusiness;
		this.geoDataBusiness = geoDataBusiness;
	}

	// create an order from apk request (json string)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void createOrder(HttpServletRequest request,
			HttpServletResponse response) {

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {

			BufferedReader reader = request.getReader();

			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception ex) {
			System.out.println("Error reading input JSON string: "
					+ ex.toString());
		}

		try {

			JSONObject createOrderObject = (JSONObject) new JSONTokener(
					stringBuffer.toString()).nextValue();

			String apiId = createOrderObject.getString("apiId");
			String apiKey = createOrderObject.getString("apiKey");

			if (deviceUtil.checkDevice(apiId, apiKey)) {

				JSONObject orderObject = createOrderObject
						.getJSONObject("order");
				Order order = OrderJsonParser.Json2Order(orderObject);

				deviceUtil.assignDevice(apiId, order);
				order.setUuid(UUID.randomUUID().toString());

				orderBusiness.save(order);

				// create new order status (Created)
				OrderStatus orderStatus = new OrderStatus();

				orderStatus.setDate(new Date());
				orderStatus.setOrder(order);
				orderStatus.setStatus(OrderStatusType.Created);

				orderStatusBusiness.save(orderStatus);

				// write order.getId(); or order.getUUID() to response stream
				// (JSON format)
				JSONObject responceJson = new JSONObject();

				responceJson.put("status", "ok");
				responceJson.put("orderId", order.getUuid().toString());

				DataOutputStream outputStream = new DataOutputStream(
						response.getOutputStream());

				outputStream.writeBytes(responceJson.toString());
				outputStream.flush();
				outputStream.close();

				System.out.println(orderObject.toString());
			} else {
				response.setStatus(403);
			}
		} catch (Exception ex) {
			System.out
					.println("Error parsing JSON to object: " + ex.toString());
		}
	}

	// get status of order
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public void getatus(HttpServletRequest request, HttpServletResponse response) {

		try {

			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);
			JSONObject getStatusObject = (JSONObject) new JSONTokener(
					stringBuffer.toString()).nextValue();

			String apiId = getStatusObject.getString("apiId");
			String apiKey = getStatusObject.getString("apiKey");

			if (deviceUtil.checkDevice(apiId, apiKey)) {

				String orderUuid = getStatusObject.getString("orderId");
				Order order = orderBusiness.getWithChilds(orderUuid);

				if (order == null) {
					response.setStatus(404);
					return;
				}

				OrderStatus status = orderStatusBusiness
						.getLastWithChilds(order);

				if (status != null) {

					JSONObject statusJson = new JSONObject();

					statusJson.put("orderId", order.getUuid().toString());

					// isn't executing by any broker
					if (order.getBroker() == null) {

						// no alacrities
						if (orderAcceptAlacrityBusiness.getAll(order) == null) {
							statusJson.put("status", "Created");
						} else {// there are any alacrities
							statusJson.put("status", "Taked");
						}

						statusJson.put("date", new Date());
					} else {// is executing by broker

						statusJson.put("status", status.getStatus().toString());
						statusJson.put("executor", order.getBroker().getName());
						statusJson.put("date", status.getDate());
					}

					DataOutputStream outputStream = new DataOutputStream(
							response.getOutputStream());

					outputStream.writeBytes(statusJson.toString());
					outputStream.flush();
					outputStream.close();
				}
			} else {
				response.setStatus(403);
			}
		} catch (Exception ex) {
			System.out
					.println("Error parsing JSON to object: " + ex.toString());
		}
	}

	// get order geo data
	@RequestMapping(value = "/geodata", method = RequestMethod.POST)
	public void getGeoData(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			StringBuffer stringBuffer = getHttpServletRequestBuffer(request);

			System.out.println(stringBuffer.toString());

			JSONObject getGeoObject = (JSONObject) new JSONTokener(
					stringBuffer.toString()).nextValue();

			String apiId = getGeoObject.getString("apiId");
			String apiKey = getGeoObject.getString("apiKey");

			if (deviceUtil.checkDevice(apiId, apiKey)) {

				String orderUuid = getGeoObject.getString("orderId");
				Order order = orderBusiness.get(orderUuid);

				if (order == null) {
					response.setStatus(404);
					return;
				}

				List<GeoData> geoDataList = null;

				// first request for order geo data
				if (getGeoObject.getString("lastDate").isEmpty()) {
					geoDataList = geoDataBusiness.getAll(order);
				} else {

					Date date = null;

					try {
						date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
								.parse(getGeoObject.getString("lastDate"));
					} catch (JSONException e) {
						System.out.println(e.toString());
					} catch (ParseException e) {
						System.out.println(e.toString());
					}

					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss.SSS");
					System.out.println(dateFormat.format(date));

					geoDataList = geoDataBusiness.getAll(order, date);
				}

				JSONObject geoDataJson = new JSONObject();
				JSONArray geoPointsArrayJson = new JSONArray();

				geoDataJson.put("orderId", order.getId());

				for (GeoData currentPoint : geoDataList) {

					JSONObject currentPointJson = new JSONObject();

					currentPointJson.put("lat", currentPoint.getLat());
					currentPointJson.put("lon", currentPoint.getLon());
					currentPointJson.put("direction",
							currentPoint.getDirection());
					currentPointJson.put("speed", currentPoint.getSpeed());
					currentPointJson
							.put("category", currentPoint.getCategory());
					currentPointJson.put("date", currentPoint.getDate());

					geoPointsArrayJson.put(currentPointJson);
				}

				geoDataJson.put("points", geoPointsArrayJson);

				DataOutputStream outputStream = new DataOutputStream(
						response.getOutputStream());

				outputStream.writeBytes(geoDataJson.toString());
				outputStream.flush();
				outputStream.close();
			} else {

				response.setStatus(403);
			}
		} catch (Exception ex) {
			System.out
					.println("Error parsing JSON to object: " + ex.toString());
		}
	}

	public StringBuffer getHttpServletRequestBuffer(HttpServletRequest request) {

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {

			BufferedReader bufferedReader = request.getReader();

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception ex) {
			System.out
					.println("Error creating string buffer from HttpServletRequest: "
							+ ex.toString());
		}

		return stringBuffer;
	}
}