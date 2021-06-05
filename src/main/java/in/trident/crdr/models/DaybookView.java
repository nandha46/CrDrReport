package in.trident.crdr.entities;

import java.util.List;

public class DaybookView {
	
	private String date;
	private String dayOfWeek;
	private String closingBal;
	private String creditTot;
	private String debitTot;
	
	private List<Transactions> transList;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getClosingBal() {
		return closingBal;
	}

	public void setClosingBal(String closingBal) {
		this.closingBal = closingBal;
	}

	public String getCreditTot() {
		return creditTot;
	}

	public void setCreditTot(String creditTot) {
		this.creditTot = creditTot;
	}

	public String getDebitTot() {
		return debitTot;
	}

	public void setDebitTot(String debitTot) {
		this.debitTot = debitTot;
	}


	public List<Transactions> getTransList() {
		return transList;
	}

	public void setTransList(List<Transactions> transList) {
		this.transList = transList;
	}

	@Override
	public String toString() {
		return "DaybookView [date=" + date + ", dayOfWeek=" + dayOfWeek + ", closingBal=" + closingBal + ", creditTot="
				+ creditTot + ", debitTot=" + debitTot + ", transList=" + transList + "]";
	}

}
