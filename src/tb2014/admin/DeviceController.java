package tb2014.admin;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IDeviceBusiness;
import tb2014.utils.NetStreamUtils;

@RequestMapping("/device")
@Controller
public class DeviceController {

	private IDeviceBusiness deviceBusiness;

	@Autowired
	public DeviceController(IDeviceBusiness deviceBusiness) {
		this.deviceBusiness = deviceBusiness;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("devices", deviceBusiness.getAll());
		return "device/list";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "device/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(@RequestParam("phone") String phone, Model model) {

		JSONObject createDeviceObject = new JSONObject();

		createDeviceObject.put("phone", phone);

		try {
			String url = "http://localhost:8080/tb2014/apidevice/device/register";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());

			wr.writeBytes(createDeviceObject.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error creating an ");
			}

			JSONObject responseJson = (JSONObject) new JSONTokener(
					NetStreamUtils.getStringFromInputStream(connection
							.getInputStream())).nextValue();

			model.addAttribute("result", responseJson);

			return "result";
		} catch (Exception ex) {

		}

		return "redirect:list";
	}
}
