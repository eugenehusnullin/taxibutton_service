package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IGeoDataBusiness;
import tb2014.dao.IGeoDataDao;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;

@Service("GeoDataBusiness")
public class GeoDataBusiness implements IGeoDataBusiness {

	private IGeoDataDao geoDataDao;

	@Autowired
	public GeoDataBusiness(IGeoDataDao geoDataDao) {
		this.geoDataDao = geoDataDao;
	}

	@Transactional(readOnly = true)
	@Override
	public GeoData get(Long id) {
		return geoDataDao.get(id);
	}

	@Transactional(readOnly = true)
	@Override
	public List<GeoData> getAll() {
		return geoDataDao.getAll();
	}

	@Transactional(readOnly = true)
	@Override
	public List<GeoData> getAll(Order order) {
		return geoDataDao.getAll(order);
	}

	@Transactional(readOnly = true)
	@Override
	public GeoData getLast(Order order) {
		return geoDataDao.getLast(order);
	}

	@Transactional
	@Override
	public void save(GeoData geoData) {
		geoDataDao.save(geoData);
	}

	@Transactional
	@Override
	public void saveOrUpdate(GeoData geoData) {
		geoDataDao.saveOrUpdate(geoData);
	}
}
