package tb2014.domain.order;

public enum VehicleClass {

	Ecomon(1), Standard(2), Business(3);

	private int value;

	private VehicleClass(int value) {
		this.value = value;
	}

	public int index() {
		return value;
	}
}
