package tb2014.business.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public List<GeoData> getAll(Order order) {
		return geoDataDao.getAll(order);
	}

	@Override
	public void save(GeoData geoData) {
		geoDataDao.save(geoData);
	}

	@Override
	public List<GeoData> getAll(Order order, Date date) {
		return geoDataDao.getAll(order, date);
	}
}
