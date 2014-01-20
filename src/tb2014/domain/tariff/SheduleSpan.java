package tb2014.domain.tariff;

import java.util.List;

public class SheduleSpan {
	
	private List<TimeInterval> timeIntervalsList;
	private List<Integer> weekdaysList;
	
	public List<TimeInterval> getTimeIntervalsList() {
		return timeIntervalsList;
	}
	
	public void setTimeIntervalsList(List<TimeInterval> timeIntervalsList) {
		this.timeIntervalsList = timeIntervalsList;
	}
	
	public List<Integer> getWeekdaysList() {
		return weekdaysList;
	}
	
	public void setWeekdaysList(List<Integer> weekdaysList) {
		this.weekdaysList = weekdaysList;
	}
}
