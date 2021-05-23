package in.trident.crdr.entities;

import java.util.ArrayList;

public class DaybookView {
	
	private String date;
	private String dayOfWeek;
	private String closingBal;
	private String creditTot;
	private String debitTot;
	
	private ArrayList<Transactions> transList;

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

	public ArrayList<Transactions> getTransList() {
		return transList;
	}

	public void setTransList(ArrayList<Transactions> transList) {
		this.transList = transList;
	}

}
