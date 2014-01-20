package tb2014.domain.tariff;

import java.util.List;

public class Description {
	
	private List<Interval> intervals;
	private List<Unit> commonOverPrice_unitsList;
	private String commonOverPrice_other;
	
	public List<Interval> getIntervals() {
		return intervals;
	}
	
	public void setIntervals(List<Interval> intervals) {
		this.intervals = intervals;
	}

	public List<Unit> getCommonOverPrice_unitsList() {
		return commonOverPrice_unitsList;
	}

	public void setCommonOverPrice_unitsList(List<Unit> commonOverPrice_unitsList) {
		this.commonOverPrice_unitsList = commonOverPrice_unitsList;
	}

	public String getCommonOverPrice_other() {
		return commonOverPrice_other;
	}

	public void setCommonOverPrice_other(String commonOverPrice_other) {
		this.commonOverPrice_other = commonOverPrice_other;
	}
}
