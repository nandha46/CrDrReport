package in.trident.crdr.models;

import java.util.List;

import in.trident.crdr.entities.Daybook;

public class DashboardAnalytics {
	private String companyName;
	private String activeYear;
	private String totalDebit;
	private String totalCredit;
	private String netFlow;
	private String openingCash;
	private String closingStock;
	private int accountCount;
	private int transactionCount;
	private List<String> monthLabels;
	private List<Double> monthDebits;
	private List<Double> monthCredits;
	private List<String> yearLabels;
	private List<Double> yearDebits;
	private List<Double> yearCredits;
	private List<Daybook> recentEntries;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getActiveYear() {
		return activeYear;
	}

	public void setActiveYear(String activeYear) {
		this.activeYear = activeYear;
	}

	public String getTotalDebit() {
		return totalDebit;
	}

	public void setTotalDebit(String totalDebit) {
		this.totalDebit = totalDebit;
	}

	public String getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(String totalCredit) {
		this.totalCredit = totalCredit;
	}

	public String getNetFlow() {
		return netFlow;
	}

	public void setNetFlow(String netFlow) {
		this.netFlow = netFlow;
	}

	public String getOpeningCash() {
		return openingCash;
	}

	public void setOpeningCash(String openingCash) {
		this.openingCash = openingCash;
	}

	public String getClosingStock() {
		return closingStock;
	}

	public void setClosingStock(String closingStock) {
		this.closingStock = closingStock;
	}

	public int getAccountCount() {
		return accountCount;
	}

	public void setAccountCount(int accountCount) {
		this.accountCount = accountCount;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	public List<String> getMonthLabels() {
		return monthLabels;
	}

	public void setMonthLabels(List<String> monthLabels) {
		this.monthLabels = monthLabels;
	}

	public List<Double> getMonthDebits() {
		return monthDebits;
	}

	public void setMonthDebits(List<Double> monthDebits) {
		this.monthDebits = monthDebits;
	}

	public List<Double> getMonthCredits() {
		return monthCredits;
	}

	public void setMonthCredits(List<Double> monthCredits) {
		this.monthCredits = monthCredits;
	}

	public List<String> getYearLabels() {
		return yearLabels;
	}

	public void setYearLabels(List<String> yearLabels) {
		this.yearLabels = yearLabels;
	}

	public List<Double> getYearDebits() {
		return yearDebits;
	}

	public void setYearDebits(List<Double> yearDebits) {
		this.yearDebits = yearDebits;
	}

	public List<Double> getYearCredits() {
		return yearCredits;
	}

	public void setYearCredits(List<Double> yearCredits) {
		this.yearCredits = yearCredits;
	}

	public List<Daybook> getRecentEntries() {
		return recentEntries;
	}

	public void setRecentEntries(List<Daybook> recentEntries) {
		this.recentEntries = recentEntries;
	}
}
