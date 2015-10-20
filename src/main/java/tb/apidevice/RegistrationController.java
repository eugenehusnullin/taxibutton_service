package tb.apidevice;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb.service.DeviceService;

@RequestMapping("/device")
@Controller("apiDeviceRegistrationController")
public class RegistrationController {
	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private DeviceService deviceService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register(HttpServletRequest request, HttpServletResponse response) {

		try {
			JSONObject requestJson = (JSONObject) new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8"))
					.nextValue();

			JSONObject responseJson = deviceService.register(requestJson);

			response.setContentType("application/json; charset=UTF-8");
			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			response.setStatus(500);
			logger.error("apiDeviceRegistrationController.register", e);
		}
	}

	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	public void confirm(HttpServletRequest request, HttpServletResponse response) {

		try {
			JSONObject requestJson = (JSONObject) new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8"))
					.nextValue();

			JSONObject responseJson = deviceService.confirm(requestJson);

			response.setContentType("application/json; charset=UTF-8");
			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			response.setStatus(500);
			logger.error("apiDeviceRegistrationController.confirm", e);
		}
	}
}
