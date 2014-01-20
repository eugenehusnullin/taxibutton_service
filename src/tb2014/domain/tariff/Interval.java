package tb2014.domain.tariff;

import java.util.List;

public class Interval {

	private String name;
	private List<SheduleSpan> sheduleIntervalsList;
	private String Special;
	private int city_minPrice;
	private String city_currency;
	private String city_included;
	private String city_other;
	private List<Destination> transfer_destinationsList;
	private List<Unit> transfer_unitsList;
	private String transfer_other;
	private String suburb_driveMkad;
	private String suburb_driveAfterMkad;
	private String suburb_startAfterMkad;
	private List<Unit> suburb_unitsList;
	private String suburb_other;
	private List<Unit> overPrice_unitsList;
	private String overPrice_other;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<SheduleSpan> getSheduleIntervalsList() {
		return sheduleIntervalsList;
	}
	
	public void setSheduleIntervalsList(List<SheduleSpan> sheduleIntervalsList) {
		this.sheduleIntervalsList = sheduleIntervalsList;
	}
	
	public String getSpecial() {
		return Special;
	}
	
	public void setSpecial(String special) {
		Special = special;
	}
	
	public int getCity_minPrice() {
		return city_minPrice;
	}
	
	public void setCity_minPrice(int city_minPrice) {
		this.city_minPrice = city_minPrice;
	}
	
	public String getCity_currency() {
		return city_currency;
	}
	
	public void setCity_currency(String city_currency) {
		this.city_currency = city_currency;
	}
	
	public String getCity_included() {
		return city_included;
	}
	
	public void setCity_included(String city_included) {
		this.city_included = city_included;
	}
	
	public String getCity_other() {
		return city_other;
	}
	
	public void setCity_other(String city_other) {
		this.city_other = city_other;
	}
	
	public List<Destination> getTransfer_destinationsList() {
		return transfer_destinationsList;
	}
	
	public void setTransfer_destinationsList(
			List<Destination> transfer_destinationsList) {
		this.transfer_destinationsList = transfer_destinationsList;
	}
	
	public List<Unit> getTransfer_unitsList() {
		return transfer_unitsList;
	}
	
	public void setTransfer_unitsList(List<Unit> transfer_unitsList) {
		this.transfer_unitsList = transfer_unitsList;
	}
	
	public String getTransfer_other() {
		return transfer_other;
	}
	
	public void setTransfer_other(String transfer_other) {
		this.transfer_other = transfer_other;
	}
	
	public String getSuburb_driveMkad() {
		return suburb_driveMkad;
	}
	
	public void setSuburb_driveMkad(String suburb_driveMkad) {
		this.suburb_driveMkad = suburb_driveMkad;
	}
	
	public String getSuburb_driveAfterMkad() {
		return suburb_driveAfterMkad;
	}
	
	public void setSuburb_driveAfterMkad(String suburb_driveAfterMkad) {
		this.suburb_driveAfterMkad = suburb_driveAfterMkad;
	}
	
	public String getSuburb_startAfterMkad() {
		return suburb_startAfterMkad;
	}
	
	public void setSuburb_startAfterMkad(String suburb_startAfterMkad) {
		this.suburb_startAfterMkad = suburb_startAfterMkad;
	}
	
	public List<Unit> getSuburb_unitsList() {
		return suburb_unitsList;
	}
	
	public void setSuburb_unitsList(List<Unit> suburb_unitsList) {
		this.suburb_unitsList = suburb_unitsList;
	}
	
	public String getSuburb_other() {
		return suburb_other;
	}
	
	public void setSuburb_other(String suburb_other) {
		this.suburb_other = suburb_other;
	}
	
	public List<Unit> getOverPrice_unitsList() {
		return overPrice_unitsList;
	}
	
	public void setOverPrice_unitsList(List<Unit> overPrice_unitsList) {
		this.overPrice_unitsList = overPrice_unitsList;
	}
	
	public String getOverPrice_other() {
		return overPrice_other;
	}
	
	public void setOverPrice_other(String overPrice_other) {
		this.overPrice_other = overPrice_other;
	}
}
