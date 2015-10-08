package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.IDeviceDao;
import tb.domain.Device;

@Repository("DeviceDao")
public class DeviceDao implements IDeviceDao {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public Device get(Long id) {
		return (Device) sessionFactory.getCurrentSession()
				.get(Device.class, id);
	}

	@Override
	public Device get(String apiId) {
		return (Device) sessionFactory.getCurrentSession()
				.createCriteria(Device.class)
				.add(Restrictions.eq("apiId", apiId)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Device> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(Device.class)
				.list();
	}

	@Override
	public void save(Device device) {
		sessionFactory.getCurrentSession().save(device);
	}

	@Override
	public Device getByPhone(String phone) {
		return (Device) sessionFactory.getCurrentSession()
				.createCriteria(Device.class)
				.add(Restrictions.eq("phone", phone))
				.add(Restrictions.or(
						Restrictions.eq("taxi", ""),
						Restrictions.isNull("taxi")))
				.uniqueResult();
	}

	@Override
	public Device get(String phone, String taxi) {
		return (Device) sessionFactory.getCurrentSession()
				.createCriteria(Device.class)
				.add(Restrictions.eq("phone", phone))
				.add(Restrictions.eq("taxi", taxi))
				.uniqueResult();
	}
}
