package tb2014.service;

import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IDeviceBusiness;
import tb2014.domain.Device;

@Service
public class DeviceService {
	@Autowired
	private IDeviceBusiness deviceBusiness;

	@Transactional
	public JSONObject register(JSONObject registerJson) {
		String phone = registerJson.optString("phone");
		String newDeviceUuid = UUID.randomUUID().toString();

		Device device = new Device();
		device.setApiId(newDeviceUuid);
		device.setPhone(phone);
		deviceBusiness.save(device);

		JSONObject resultJson = new JSONObject();
		resultJson.put("apiId", device.getApiId());
		return resultJson;
	}
}
