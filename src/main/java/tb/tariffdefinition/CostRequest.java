package tb.tariffdefinition;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tb.domain.TariffDefinition;
import tb.domain.maparea.Point;
import tb.domain.order.VehicleClass;
import tb.service.serialize.YandexOrderSerializer;
import tb.utils.HttpUtils;

@Service
public class CostRequest {
	private static final Logger logger = LoggerFactory.getLogger(CostRequest.class);

	@Autowired
	private TariffDefinitionHelper tariffDefinitionHelper;

	@Value("#{mainSettings['costservice.url']}")
	private String costServiceUrl;

	public JSONObject getCost(Point source, List<Point> destinations, VehicleClass vehicleClass, Date bookDate,
			List<String> adds) {
		TariffDefinition tariffDefinition = getTariffDefinition(source, vehicleClass);

		JSONObject requestJson = new JSONObject();

		requestJson.put("RoutingServiceName", tariffDefinition.getRoutingServiceName() == null ? "YandexMapsService"
				: tariffDefinition.getRoutingServiceName());
		requestJson.put("TaxiServiceId", "taxirf");
		requestJson.put("BookingTime", getBookDate(bookDate));
		requestJson.put("TariffName", tariffDefinition.getIdName());
		requestJson.put("Source", getJsonPoint(source));
		if (destinations != null) {
			requestJson.put("Destinations", getJsonPoints(destinations));
		}
		if (adds != null) {
			requestJson.put("AdditionalServices", getAdds(adds));
		}

		logger.info("COST JSON: " + requestJson.toString());

		try {
			HttpURLConnection connection = HttpUtils.postRawData(requestJson.toString(), costServiceUrl, "UTF-8");
			if (connection.getResponseCode() == 200) {
				String responseString = IOUtils.toString(connection.getInputStream());
				logger.info("COST SUCCESS: " + responseString);
				JSONObject responseJson = (JSONObject) new JSONTokener(responseString).nextValue();

				JSONObject costJson = new JSONObject();
				costJson.put("sum", responseJson.get("Price"));
				JSONObject responseNestedJson = responseJson.getJSONObject("Calculated");
				costJson.put("km", responseNestedJson.getDouble("Km"));
				costJson.put("min", responseNestedJson.getDouble("Min"));

				return costJson;
			} else {
				logger.warn("COST FAILURE: ResponseCode=" + connection.getResponseCode());
			}
		} catch (IOException e) {
			logger.error("getCost", e);
		}
		return null;
	}

	private JSONArray getAdds(List<String> adds) {
		JSONArray array = new JSONArray();
		for (String add : adds) {
			String yandexAdd = YandexOrderSerializer.defineRequireName(add);
			array.put(yandexAdd);
		}
		return array;
	}

	private JSONArray getJsonPoints(List<Point> points) {
		JSONArray array = new JSONArray();
		for (Point point : points) {
			array.put(getJsonPoint(point));
		}
		return array;
	}

	private JSONObject getJsonPoint(Point point) {
		JSONObject pointJson = new JSONObject();
		pointJson.put("Lat", point.getLatitude());
		pointJson.put("Lon", point.getLongitude());
		return pointJson;
	}

	private String getBookDate(Date bookDate) {
		// "2008-12-28T00:00:00",
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return dateFormat.format(bookDate);
	}

	private TariffDefinition getTariffDefinition(Point source, VehicleClass vehicleClass) {
		return tariffDefinitionHelper.getTariffDefinition(source.getLatitude(),
				source.getLongitude(), vehicleClass);
	}
}
