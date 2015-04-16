package tb.service;

import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import tb.domain.maparea.MapArea;
import tb.domain.maparea.Point;
import tb.domain.maparea.Polygon;

@Service
public class MapAreaAssist {
	private Map<Long, Path2D> polygons;

	public MapAreaAssist() {
		polygons = new HashMap<Long, Path2D>();
	}

	public boolean contains(MapArea mapArea, double lat, double lon) {
		if (mapArea instanceof Polygon) {
			return checkPolygon((Polygon) mapArea, lat, lon);
		}
		return false;
	}
	
	public void updateMapArea(MapArea mapArea) {
		if (mapArea instanceof Polygon) {
			putPolygon((Polygon) mapArea);
		}
	}

	private boolean checkPolygon(Polygon polygon, double lat, double lon) {
		Path2D path2D = polygons.get(polygon.getId());
		if (path2D == null) {
			path2D = putPolygon(polygon);
		}
		return path2D.contains(lat, lon);
	}

	private Path2D putPolygon(Polygon polygon) {
		Path2D path2D = new Path2D.Double();
		boolean isFirst = true;
		for (Point point : polygon.getPoints()) {
			if (isFirst) {
				path2D.moveTo(point.getLatitude(), point.getLongitude());
				isFirst = false;
			} else {
				path2D.lineTo(point.getLatitude(), point.getLongitude());
			}
		}
		path2D.closePath();
		polygons.put(polygon.getId(), path2D);
		return path2D;
	}

}
