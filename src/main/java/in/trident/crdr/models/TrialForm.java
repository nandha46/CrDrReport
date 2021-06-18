package in.trident.crdr.models;

import java.util.List;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 18 Jun 2021
 * 
 * @version 0.0.5b
 *
 */


public class TrialForm {

	private boolean reportOrder;
	private String endDate;
	private boolean zeroBal;
	private int level;
	private List<Integer> accCode;
	
	public boolean isReportOrder() {
		return reportOrder;
	}
	public void setReportOrder(boolean reportOrder) {
		this.reportOrder = reportOrder;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public boolean isZeroBal() {
		return zeroBal;
	}
	public void setZeroBal(boolean zeroBal) {
		this.zeroBal = zeroBal;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public List<Integer> getAccCode() {
		return accCode;
	}
	public void setAccCode(List<Integer> accCode) {
		this.accCode = accCode;
	}
	
	@Override
	public String toString() {
		return "TrialForm [reportOrder=" + reportOrder + ", endDate=" + endDate + ", zeroBal=" + zeroBal + ", level="
				+ level + ", accCode=" + accCode + "]";
	}	
	
	
}
