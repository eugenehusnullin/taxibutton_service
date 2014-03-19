package tb2014.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.admin.model.DeviceModel;
import tb2014.dao.IDeviceDao;
import tb2014.domain.Device;

@Service
public class DeviceService {
	@Autowired
	private IDeviceDao deviceDao;

	@Transactional
	public JSONObject register(JSONObject registerJson) {
		String phone = registerJson.optString("phone");
		String newDeviceUuid = UUID.randomUUID().toString();

		Device device = new Device();
		device.setApiId(newDeviceUuid);
		device.setPhone(phone);
		deviceDao.save(device);

		JSONObject resultJson = new JSONObject();
		resultJson.put("apiId", device.getApiId());
		return resultJson;
	}
	
	@Transactional
	public List<DeviceModel> getAll() {
		List<Device> devices = deviceDao.getAll();
		
		List<DeviceModel> models = new ArrayList<>();
		for (Device device : devices) {
			DeviceModel model = new DeviceModel();
			model.setId(device.getId());
			model.setApiId(device.getApiId());
			model.setPhone(device.getPhone());			
			models.add(model);
		}
		
		return models;
	}
}
