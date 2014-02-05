package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional(readOnly = true)
	@Override
	public Device get(Long id) {
		return deviceDao.get(id);
	}

	@Transactional(readOnly = true)
	@Override
	public Device get(String apiId) {
		return deviceDao.get(apiId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Device> getAll() {
		return deviceDao.getAll();
	}

	@Transactional
	@Override
	public void delete(Device device) {
		deviceDao.delete(device);
	}

	@Transactional
	@Override
	public void save(Device device) {
		deviceDao.save(device);
	}

	@Transactional
	@Override
	public void saveOrUpdate(Device device) {
		deviceDao.save(device);
	}
}
