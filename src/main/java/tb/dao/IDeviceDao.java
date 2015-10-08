package tb.dao;

import java.util.List;

import tb.domain.Device;

public interface IDeviceDao {

	public Device get(Long id);

	public Device get(String apiId);

	public List<Device> getAll();

	public void save(Device device);

	public Device getByPhone(String phone);

	public Device get(String phone, String taxi);
}
