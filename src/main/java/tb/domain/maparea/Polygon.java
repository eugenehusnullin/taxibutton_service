package tb.domain.maparea;

import java.awt.geom.Path2D;
import java.util.List;

public class Polygon extends MapArea {

	private List<Point> points;

	@Override
	public boolean contains(double x, double y) {
		Path2D boundary = new Path2D.Double();
		boolean isFirst = true;
		for (Point point : points) {
			if (isFirst) {
				boundary.moveTo(point.getLatitude(), point.getLongitude());
				isFirst = false;
			} else {
				boundary.lineTo(point.getLatitude(), point.getLongitude());
			}
		}
		boundary.closePath();

		return boundary.contains(x, y);
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
