package tb2014.dao;

import java.util.List;

import tb2014.domain.Device;

public interface IDeviceDao {

	public Device get(Long id);
	
	public Device get(String apiId);
	
	public List<Device> getAll();
	
	public void save(Device device);
	
	public Device getByPhone(String phone);
}
