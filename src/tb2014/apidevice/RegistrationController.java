package tb2014.apidevice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hsqldb.lib.DataOutputStream;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.service.DeviceService;
import tb2014.utils.NetStreamUtils;

@RequestMapping("/device")
@Controller("apiDeviceRegistrationController")
public class RegistrationController {
	private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private DeviceService deviceService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register(HttpServletRequest request, HttpServletResponse response) {

		try {
			StringBuffer requestBuffer = NetStreamUtils.getHttpServletRequestBuffer(request);
			JSONObject requestJson = (JSONObject) new JSONTokener(requestBuffer.toString()).nextValue();

			JSONObject responseJson = deviceService.register(requestJson);

			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			response.setStatus(500);
			log.error("apiDeviceRegistrationController.register", e);
		}
	}

	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public void confirm(HttpServletRequest request, HttpServletResponse response) {

		try {
			StringBuffer requestBuffer = NetStreamUtils.getHttpServletRequestBuffer(request);
			JSONObject requestJson = (JSONObject) new JSONTokener(requestBuffer.toString()).nextValue();

			JSONObject responseJson = deviceService.register(requestJson);

			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			response.setStatus(500);
			log.error("apiDeviceRegistrationController.confirm", e);
		}
	}

}
