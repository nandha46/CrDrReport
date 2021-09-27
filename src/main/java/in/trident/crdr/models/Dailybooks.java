package in.trident.crdr.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

/**
 * @author Nandhakumar Subramanian
 * 
 * @since 0.0.5
 * 
 */

public class Dailybooks {
	
	private String date,narration,debitAmt,creditAmt,balance,debitOrCredit;
	
	SimpleDateFormat outsdf= new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat insdf= new SimpleDateFormat("yyyy-MM-dd");
	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));
	
	public Dailybooks() {
		
	}
	
	public Dailybooks(String date, String narration, Double debitAmt, Double creditAmt, String balance, String debitOrCredit) {
		Date date1 = new Date();
		try {
			date1 = insdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.date = outsdf.format(date1);
		this.narration = narration;
		this.debitAmt = nf.format(debitAmt).toString();
		this.creditAmt = nf.format(creditAmt).toString();
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
