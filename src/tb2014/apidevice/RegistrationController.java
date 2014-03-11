package tb2014.apidevice;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.lib.DataOutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.business.IDeviceBusiness;
import tb2014.domain.Device;
import tb2014.utils.NetStreamUtils;

@RequestMapping("/device")
@Controller("apiDeviceRegistrationController")
public class RegistrationController {

	@Autowired
	private IDeviceBusiness deviceBusiness;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register(HttpServletRequest request, HttpServletResponse response) {

		try {
			StringBuffer requestBuffer = NetStreamUtils.getHttpServletRequestBuffer(request);

			System.out.println(requestBuffer.toString());
			JSONObject requestJson = (JSONObject) new JSONTokener(requestBuffer.toString()).nextValue();

			String phone = null;

			try {
				phone = requestJson.getString("phone");
			} catch (JSONException ex) {
				phone = "";
			}

			Device device = new Device();
			String resultUuid = UUID.randomUUID().toString();

			device.setApiId(resultUuid);
			device.setPhone(phone);

			deviceBusiness.save(device);

			JSONObject responseJson = new JSONObject();

			responseJson.put("apiId", device.getApiId());

			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());

			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();

		} catch (Exception ex) {
			System.out.println("Error register new device: " + ex.toString());
		}
	}
}
