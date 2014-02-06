package tb2014.business;

import java.util.List;

import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;

public interface IGeoDataBusiness {

	GeoData get(Long id);

	List<GeoData> getAll();

	List<GeoData> getAll(Order order);

	GeoData getLast(Order order);

	void save(GeoData geoData);

	void saveOrUpdate(GeoData geoData);
}
