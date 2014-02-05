package tb2014.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IDeviceBusiness;
import tb2014.domain.Device;
import tb2014.domain.order.Order;

@Service("DeviceUtil")
public class DeviceUtil {

	private IDeviceBusiness deviceBusiness;

	@Autowired
	public DeviceUtil(IDeviceBusiness deviceBusiness) {
		this.deviceBusiness = deviceBusiness;
	}

	public Boolean checkDevice(String apiId, String apiKey) {

		Device device = deviceBusiness.get(apiId);

		if (device == null) {
			return false;
		}

		if (device.getApiKey().trim().equals(apiKey.trim()) == false) {
			return false;
		}

		return true;
	}
	
	public Order assignDevice(String apiId, Order order) {
		
		Device device = deviceBusiness.get(apiId);
		order.setDevice(device);
		
		return order;
	}
}
