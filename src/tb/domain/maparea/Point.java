package tb.domain.maparea;


public class Point {
	private double latitude;
	private double longitude;

	public Point() {
	}

	public Point(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double value) {
		this.latitude = value;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double value) {
		this.longitude = value;
	}
}
