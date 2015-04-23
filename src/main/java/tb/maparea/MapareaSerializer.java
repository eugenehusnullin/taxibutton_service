package tb.maparea;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import tb.domain.maparea.MapArea;
import tb.domain.maparea.Point;
import tb.domain.maparea.Polygon;

@Service
public class MapareaSerializer {

	public JSONObject serialize(Set<MapArea> mapareas) {
		JSONObject result = new JSONObject();
		for (MapArea mapArea : mapareas) {
			if (mapArea instanceof Polygon) {
				Polygon polygon = (Polygon) mapArea;

				JSONArray firstCoordinates = new JSONArray();
				for (Point point : polygon.getPoints()) {
					JSONArray item = new JSONArray();
					item.put(point.getLatitude());
					item.put(point.getLongitude());

					firstCoordinates.put(item);
				}

				JSONArray coordinates = new JSONArray();
				coordinates.put(firstCoordinates);

				JSONObject jsonMaparea = new JSONObject();
				jsonMaparea.put("coordinates", coordinates);

				result.put(polygon.getName(), jsonMaparea);
			}
		}

		return result;
	}
}
