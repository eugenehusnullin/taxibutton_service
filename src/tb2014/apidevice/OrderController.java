package tb2014.apidevice;

import java.io.BufferedReader;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.lib.DataOutputStream;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.service.serialize.OrderJsonParser;
import tb2014.utils.DeviceUtil;

@RequestMapping("/apidevice/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private IOrderBusiness orderBusiness;
	private IOrderStatusBusiness orderStatusBusiness;
	private IOrderAcceptAlacrityBusiness orderAcceptAlacrityBusiness;
	private DeviceUtil deviceUtil;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness, DeviceUtil deviceUtil,
			IOrderStatusBusiness orderStatusBusines,
			IOrderAcceptAlacrityBusiness orderAcceptAlacrityBusiness) {
		this.orderBusiness = orderBusiness;
		this.deviceUtil = deviceUtil;
		this.orderStatusBusiness = orderStatusBusines;
		this.orderAcceptAlacrityBusiness = orderAcceptAlacrityBusiness;
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
	public void gtatus(HttpServletRequest request, HttpServletResponse response) {

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

			JSONObject getStatusObject = (JSONObject) new JSONTokener(
					stringBuffer.toString()).nextValue();

			String apiId = getStatusObject.getString("apiId");
			String apiKey = getStatusObject.getString("apiKey");

			if (deviceUtil.checkDevice(apiId, apiKey)) {

				String orderUuid = getStatusObject.getString("orderId");
				Order order = orderBusiness.get(orderUuid);

				if (order == null) {
					response.setStatus(404);
					return;
				}

				OrderStatus status = orderStatusBusiness.getLast(order);
				JSONObject statusJson = new JSONObject();

				statusJson.put("orderId", status.getOrder().getId().toString());

				if (order.getBroker() == null) {// isn't executing by any broker

					// no alacrities
					if (orderAcceptAlacrityBusiness.getAll(order) == null) {
						statusJson.put("status", "NoAlacrity");
					} else {// there are any alacrities
						statusJson.put("status", "IsAlacrity");
					}

					statusJson.put("date", new Date());
				} else {// is executing by broker

					statusJson.put("status", status.getStatus().toString());
					statusJson.put("executor", status.getOrder().getBroker()
							.getName());
					statusJson.put("date", status.getDate());
				}

				DataOutputStream outputStream = new DataOutputStream(
						response.getOutputStream());

				outputStream.writeBytes(statusJson.toString());
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
}
