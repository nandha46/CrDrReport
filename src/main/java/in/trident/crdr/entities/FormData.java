package in.trident.crdr.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormData {

	private String startDate;
	private String endDate;
	private boolean reportOrder;
	private boolean transactedAccOnly;
	private boolean cutOff;
	private String date;
	private boolean zeroBal;
	private int level;
	
	public int findDays(String d1,String d2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long diff=0l;
		try {
			diff = df.parse(d2).getTime() - df.parse(d1).getTime();
			diff /= 1000*60*60*24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int)diff;
	}
	
	
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


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
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

}
