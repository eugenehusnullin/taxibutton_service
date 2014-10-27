package tb2014.domain.maparea;

public abstract class MapArea {

	private Long id;
	private String name;
	private String about;

	public abstract boolean contains(double x, double y);

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

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
}
