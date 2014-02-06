package tb2014.apidevice;

import java.io.BufferedReader;
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
import tb2014.domain.order.Order;
import tb2014.service.serialize.Json2OrderParse;
import tb2014.utils.DeviceUtil;

@RequestMapping("/apidevice/order")
@Controller("apiDeviceOrderController")
public class OrderController {

	private IOrderBusiness orderBusiness;
	private DeviceUtil deviceUtil;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness, DeviceUtil deviceUtil) {
		this.orderBusiness = orderBusiness;
		this.deviceUtil = deviceUtil;
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
				Order order = Json2OrderParse.Json2Order(orderObject);

				deviceUtil.assignDevice(apiId, order);
				order.setUuid(UUID.randomUUID().toString());

				orderBusiness.save(order);

				// write order.getId(); or order.getUUID() to response stream
				// (JSON format)
				JSONObject responceObject = new JSONObject();

				responceObject.put("status", "ok");
				responceObject.put("orderId", order.getUuid().toString());

				DataOutputStream outputStream = new DataOutputStream(
						response.getOutputStream());

				outputStream.writeBytes(responceObject.toString());
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
}
