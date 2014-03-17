package tb2014.admin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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

import tb2014.service.DeviceService;
import tb2014.utils.NetStreamUtils;

@RequestMapping("/device")
@Controller
public class DeviceController {

	@Autowired
	private DeviceService deviceService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("devices", deviceService.getAll());
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
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(createDeviceObject.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error creating an ");
			}

			JSONObject responseJson = (JSONObject) new JSONTokener(NetStreamUtils.getStringFromInputStream(connection
					.getInputStream())).nextValue();

			model.addAttribute("result", responseJson);

			return "result";
		} catch (Exception ex) {
			System.out.println("Error creating device: " + ex.toString());
		}

		return "redirect:list";
	}

	@RequestMapping(value = "/tariffs", method = RequestMethod.GET)
	public String getTariffs(@RequestParam("apiId") String apiId, Model model) {

		try {
			JSONObject requestJson = new JSONObject();
			requestJson.put("apiId", apiId);

			String url = "http://localhost:8080/tb2014/apidevice/tariff/get";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

			wr.writeBytes(requestJson.toString());
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				System.out.println("Error getting tariffs");
			}

			StringBuffer stringBuffer = new StringBuffer();
			String line = null;

			try {

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
						"UTF-8"));

				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
			} catch (Exception ex) {
				System.out.println("Error receiving server respone: " + ex.toString());
			}

			System.out.println(stringBuffer.toString());
			model.addAttribute("result", stringBuffer.toString());
		} catch (Exception ex) {

			model.addAttribute("result", "Error");
			System.out.println("Error receiving tariffs: " + ex.toString());
		}

		return "result";
	}
}
