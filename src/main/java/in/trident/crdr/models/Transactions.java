package in.trident.crdr.models;

import java.util.Locale;
/**
 * @author Nandhakumar Subramanian
 */

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

public class Transactions implements Comparable<Transactions>{

	private Integer sNo;
	private String creditAmt, debitAmt;
	private String narration;
	private int stkValue;
	private String shortName;
	
	private static final Double DOUBLE = (double) 0;

	public Transactions () {
		
	}
	
	public Transactions(Integer sNo, Double creditAmt, Double debitAmt, String narration, int stkValue, String shortName) {
		
	//	NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en","in")).precision(Precision.fixedFraction(2));
		
		this.sNo = sNo;
		
		if ( creditAmt.equals(DOUBLE)) {
			this.creditAmt = "";
		} else {
			this.creditAmt = nf.format(creditAmt).toString();
		}
		
		if (debitAmt.equals(DOUBLE)) {
			this.debitAmt = "";
		} else {
			this.debitAmt = nf.format(debitAmt).toString();
		}
		this.narration = narration;
		this.stkValue = stkValue;
		this.shortName = shortName;
	}
	
	public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}
	public String getCreditAmt() {
		return creditAmt;
	}
	public void setCreditAmt(String creditAmt) {
		this.creditAmt = creditAmt;
	}
	public String getDebitAmt() {
		return debitAmt;
	}
	public void setDebitAmt(String debitAmt) {
		this.debitAmt = debitAmt;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public int getStkValue() {
		return stkValue;
	}
	public void setStkValue(int stkValue) {
		this.stkValue = stkValue;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Override
	public String toString() {
		return "Transactions [sNo=" + sNo + ", creditAmt=" + creditAmt + ", debitAmt=" + debitAmt + ", narration="
				+ narration + ", stkValue=" + stkValue + ", shortName=" + shortName + "]";
	}

	@Override
	public int compareTo(Transactions o) {
		return this.sNo.compareTo(o.sNo);
	}
	
	
}
