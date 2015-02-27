package tb.dao;

import java.util.Date;
import java.util.List;

import tb.domain.order.GeoData;
import tb.domain.order.Order;

public interface IGeoDataDao {

	List<GeoData> getAll(Order order);

	List<GeoData> getAll(Order order, Date date);

	void save(GeoData geoData);
}
