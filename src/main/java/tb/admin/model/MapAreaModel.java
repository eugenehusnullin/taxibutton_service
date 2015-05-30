package tb.admin.model;

import java.util.List;

import tb.domain.maparea.Point;

public class MapAreaModel {
	private Long id;
	private String name;
	private List<Point> points;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

}
