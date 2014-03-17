package tb2014.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.admin.model.GeodataModel;
import tb2014.business.impl.GeoDataBusiness;
import tb2014.business.impl.OrderBusiness;
import tb2014.domain.order.GeoData;

@Service
public class GeodataService {

	@Autowired
	private OrderBusiness orderBusiness;
	@Autowired
	private GeoDataBusiness geoDataBusiness;

	@Transactional
	public List<GeodataModel> getGeodata(Long orderId) {
		List<GeoData> geoDatas = geoDataBusiness.getAll(orderBusiness.get(orderId));

		List<GeodataModel> models = new ArrayList<>();
		for (GeoData geoData : geoDatas) {
			GeodataModel model = new GeodataModel();
			models.add(model);

			model.setId(geoData.getId());
			model.setDate(geoData.getDate());
			model.setLat(geoData.getLat());
			model.setLon(geoData.getLon());
		}

		return models;
	}
}
