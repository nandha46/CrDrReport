package in.trident.crdr.models;

import java.math.BigDecimal;

import in.trident.crdr.util.FormatUtil;

/**
 * @author Nandhakumar Subramanian
 * 
 * @since 0.0.5
 * 
 */
public class Dailybooks {

	private String date;
	private String narration;
	private String debitAmt;
	private String creditAmt;
	private String balance;
	private String debitOrCredit;

	public Dailybooks() {
	}

	public Dailybooks(String date, String narration, Double debitAmt, Double creditAmt, String balance,
			String debitOrCredit) {
		this.date = FormatUtil.formatDisplayDate(date);
		this.narration = narration;
		BigDecimal dr = FormatUtil.nvl(debitAmt);
		BigDecimal cr = FormatUtil.nvl(creditAmt);
		this.debitAmt = dr.compareTo(BigDecimal.ZERO) == 0 ? "" : FormatUtil.formatAmount(dr);
		this.creditAmt = cr.compareTo(BigDecimal.ZERO) == 0 ? "" : FormatUtil.formatAmount(cr);
		this.balance = balance;
		this.debitOrCredit = debitOrCredit;
	}

	public Dailybooks(String narration, String debitAmt, String creditAmt) {
		this.date = "";
		this.narration = narration;
		this.debitAmt = debitAmt;
		this.creditAmt = creditAmt;
		this.balance = "";
		this.debitOrCredit = "";
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getDebitAmt() {
		return debitAmt;
	}

	public void setDebitAmt(String debitAmt) {
		this.debitAmt = debitAmt;
	}

	public String getCreditAmt() {
		return creditAmt;
	}

	public void setCreditAmt(String creditAmt) {
		this.creditAmt = creditAmt;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getDebitOrCredit() {
		return debitOrCredit;
	}

	public void setDebitOrCredit(String debitOrCredit) {
		this.debitOrCredit = debitOrCredit;
	}

	@Override
	public String toString() {
		return "Dailybooks [date=" + date + ", narration=" + narration + ", debitAmt=" + debitAmt + ", creditAmt="
				+ creditAmt + ", balance=" + balance + ", debitOrCredit=" + debitOrCredit + "]";
	}
}
