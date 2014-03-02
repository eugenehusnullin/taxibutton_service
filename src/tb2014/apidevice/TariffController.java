package tb2014.apidevice;

import java.util.List;

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

import tb2014.business.ISimpleTariffBusiness;
import tb2014.domain.tariff.SimpleTariff;
import tb2014.utils.DeviceUtil;
import tb2014.utils.NetStreamUtils;

@RequestMapping("/tariff")
@Controller("apiDeviceTariffController")
public class TariffController {

	private ISimpleTariffBusiness simpleTariffBusiness;
	private DeviceUtil deviceUtil;

	@Autowired
	public TariffController(ISimpleTariffBusiness simpleTariffBusiness, DeviceUtil deviceUtil) {

		this.simpleTariffBusiness = simpleTariffBusiness;
		this.deviceUtil = deviceUtil;
	}

	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public void getAll(HttpServletRequest request, HttpServletResponse response) {

		try {

			StringBuffer stringBuffer = NetStreamUtils.getHttpServletRequestBuffer(request);
			JSONObject requestJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();
			int statusCode = 0;
			String apiId = null;

			try {
				apiId = requestJson.getString("apiId");
			} catch (JSONException ex) {
				statusCode = 403;
				response.setStatus(statusCode);
				return;
			}

			if (deviceUtil.checkDevice(apiId)) {

				List<SimpleTariff> tariffs = simpleTariffBusiness.getAllWithChilds();
				JSONArray responseJson = new JSONArray();

				for (SimpleTariff currentTariff : tariffs) {

					JSONObject currentTariffJson = new JSONObject();

					currentTariffJson.put("brokerId", currentTariff.getBroker().getUuid());
					currentTariffJson.put("tariff", currentTariff.getTariffs());
					responseJson.put(currentTariffJson);
				}

				DataOutputStream outputStream = new DataOutputStream(response.getOutputStream());
				byte[] bytes = responseJson.toString().getBytes("UTF-8");

				outputStream.write(bytes);
				outputStream.flush();
				outputStream.close();
			} else {
				response.setStatus(403);
			}
		} catch (Exception ex) {
			System.out.println("Error creating tariffs JSON: " + ex.toString());
		}
	}
}
