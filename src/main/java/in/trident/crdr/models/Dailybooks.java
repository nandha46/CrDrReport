package in.trident.crdr.models;

/**
 * @author Nandhakumar Subramanian
 * 
 * @since 0.0.5
 * 
 */

public class Dailybooks {
	
	private String date,narration,debitAmt,creditAmt,balance,debitOrCredit;
	
	public Dailybooks() {
		
	}
	
	public Dailybooks(String date, String narration, String debitAmt, String creditAmt, String balance, String debitOrCredit) {
		this.date = date;
		this.narration = narration;
		this.debitAmt = debitAmt;
		this.creditAmt = creditAmt;
		this.balance = balance;
		this.debitOrCredit = debitOrCredit;
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
