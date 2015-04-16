package tb.domain.maparea;

import java.util.List;

public class Polygon extends MapArea {

	private List<Point> points;

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
