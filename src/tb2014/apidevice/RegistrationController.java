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
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.service.DeviceService;
import tb2014.utils.NetStreamUtils;
import tb2014.utils.Sms48;
import tb2014.utils.SmsSelf;

@RequestMapping("/device")
@Controller("apiDeviceRegistrationController")
public class RegistrationController {
	private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	private DeviceService deviceService;
	@Autowired
	private Sms48 sms48;
	@Autowired
	private SmsSelf smsSelf;

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

			JSONObject responseJson = deviceService.confirm(requestJson);

			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(responseJson.toString());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			response.setStatus(500);
			log.error("apiDeviceRegistrationController.confirm", e);
		}
	}

	@RequestMapping(value = "/simplesendsms", method = RequestMethod.POST)
	public void simpleSendSms(HttpServletResponse response, @RequestParam("phone") String phone,
			@RequestParam("key") int key, @RequestParam("sender") String sender) {
		try {
			sms48.send(phone, "Код: " + Integer.toString(key), sender);
			response.setStatus(200);
		} catch (Exception e) {
			response.setStatus(500);
			log.error("apiDeviceRegistrationController.simplesendsms", e);
		}
	}

	@RequestMapping(value = "/getsms", method = RequestMethod.GET)
	public void getSms(HttpServletResponse response) {
		try {
			String send = smsSelf.get4Send();
			DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
			outputStream.writeBytes(send);
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			response.setStatus(500);
			log.error("apiDeviceRegistrationController.getSms", e);
		}
	}

}
