package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IDeviceBusiness;
import tb2014.dao.IDeviceDao;
import tb2014.domain.Device;

@Service("DeviceBusiness")
public class DeviceBusiness implements IDeviceBusiness {

	private IDeviceDao deviceDao;
	
	@Autowired
	public DeviceBusiness(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
	
	@Override
	public Device get(Long id) {
		return deviceDao.get(id);
	}

	@Override
	public Device get(String apiId) {
		return deviceDao.get(apiId);
	}

	@Override
	public List<Device> getAll() {
		return deviceDao.getAll();
	}

	@Override
	public void save(Device device) {
		deviceDao.save(device);
	}
}
