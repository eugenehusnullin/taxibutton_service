package tb2014.dao;

import java.util.Date;
import java.util.List;

import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;

public interface IGeoDataDao {

	List<GeoData> getAll(Order order);

	List<GeoData> getAll(Order order, Date date);

	void save(GeoData geoData);
}
