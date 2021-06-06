package in.trident.crdr.models;

import java.util.List;
/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @since 0.0.5
 */

public class LedgerForm {
	
	private String startDate;
	private String endDate;
	private boolean reportOrder;
	private boolean transactedAccOnly;
	private boolean cutOff;
	private boolean zeroBal;
	private boolean stockNeeded;
	private List<Integer> accCode;
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public boolean isReportOrder() {
		return reportOrder;
	}
	public void setReportOrder(boolean reportOrder) {
		this.reportOrder = reportOrder;
	}
	public boolean isTransactedAccOnly() {
		return transactedAccOnly;
	}
	public void setTransactedAccOnly(boolean transactedAccOnly) {
		this.transactedAccOnly = transactedAccOnly;
	}
	public boolean isCutOff() {
		return cutOff;
	}
	public void setCutOff(boolean cutOff) {
		this.cutOff = cutOff;
	}
	
	public boolean isZeroBal() {
		return zeroBal;
	}
	public void setZeroBal(boolean zeroBal) {
		this.zeroBal = zeroBal;
	}

	public boolean isStockNeeded() {
		return stockNeeded;
	}
	public void setStockNeeded(boolean stockNeeded) {
		this.stockNeeded = stockNeeded;
	}
	public List<Integer> getAccCode() {
		return accCode;
	}
	public void setAccCode(List<Integer> accCode) {
		this.accCode = accCode;
	}
	
}
