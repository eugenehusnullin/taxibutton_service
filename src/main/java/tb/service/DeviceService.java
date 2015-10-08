package tb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.admin.model.DeviceModel;
import tb.dao.IDeviceDao;
import tb.domain.Device;

@Service
public class DeviceService {
	@Autowired
	private IDeviceDao deviceDao;
	@Autowired
	private SmsService smsService;

	@Transactional
	public JSONObject register(JSONObject registerJson) {
		JSONObject resultJson = new JSONObject();

		String phone = registerJson.optString("phone");

		if (phone.isEmpty()) {
			resultJson.put("result", "ERROR");
			resultJson.put("description", "bad params (not exist phone)");
			return resultJson;
		}

		String taxi = registerJson.optString("taxi");

		Device device = null;
		if (taxi.isEmpty()) {
			device = deviceDao.getByPhone(phone);
		} else {
			device = deviceDao.get(phone, taxi);
		}

		if (device == null) {
			String newDeviceUuid = UUID.randomUUID().toString();
			device = new Device();
			device.setApiId(newDeviceUuid);
			device.setPhone(phone);
			device.setTaxi(taxi);
			device.setRegDate(new Date());
		}

		device.setConfirmDate(new Date());
		Random random = new Random();
		int keyInt = random.nextInt(9999 - 1000) + 1000;
		device.setConfirmKey(Integer.toString(keyInt));
		deviceDao.save(device);

		boolean smsResult = smsService.send(taxi, phone, "Код: " + Integer.toString(keyInt));
		resultJson.put("result", smsResult ? "WAITSMS" : "ERROR");
		return resultJson;
	}

	@Transactional
	public JSONObject confirm(JSONObject confirmJson) {
		JSONObject resultJson = new JSONObject();

		String phone = confirmJson.optString("phone");
		String key = confirmJson.optString("key");

		if (phone.isEmpty() || key.isEmpty()) {
			resultJson.put("result", "ERROR");
			resultJson.put("description", "bad params (not exist phone or key)");
			return resultJson;
		}

		String taxi = confirmJson.optString("taxi");

		Device device = null;
		if (taxi.isEmpty()) {
			device = deviceDao.getByPhone(phone);
		} else {
			device = deviceDao.get(phone, taxi);
		}

		if (device == null) {
			resultJson.put("result", "ERROR");
			resultJson.put("description", "phone not found");
			return resultJson;
		}

		if (!device.getConfirmKey().equals(key)) {
			resultJson.put("result", "WRONGKEY");
			return resultJson;
		} else {
			device.setConfirmedDate(new Date());
			deviceDao.save(device);
			resultJson.put("result", "OK");
			resultJson.put("apiId", device.getApiId());
			return resultJson;
		}
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
			model.setConfirmKey(device.getConfirmKey());
			models.add(model);
		}

		return models;
	}
}
