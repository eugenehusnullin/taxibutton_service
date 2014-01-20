package tb2014.domain.tariff;

import java.util.List;

public class Destination {

	private String type;
	private String name;
	private List<DestinationSource> soursesList;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<DestinationSource> getSoursesList() {
		return soursesList;
	}
	
	public void setSoursesList(List<DestinationSource> soursesList) {
		this.soursesList = soursesList;
	}
}
