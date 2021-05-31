package in.trident.crdr.entities;

import java.util.Locale;
/**
 * @author Nandhakumar Subramanian
 */

import com.ibm.icu.text.NumberFormat;

public class Transactions implements Comparable<Transactions>{

	private Integer sNo;
	private String creditAmt, debitAmt;
	private String narration;
	private int stkValue;
	private static final Double DOUBLE = (double) 0;

	public Transactions () {
		
	}
	
	public Transactions(Integer sNo, Double creditAmt, Double debitAmt, String narration, int stkValue) {
		
		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		
		this.sNo = sNo;
		
		if ( creditAmt.equals(DOUBLE)) {
			this.creditAmt = "";
		} else {
			this.creditAmt = nf.format(creditAmt);
		}
		
		if (debitAmt.equals(DOUBLE)) {
			this.debitAmt = "";
		} else {
			this.debitAmt = nf.format(debitAmt);
		}
		this.narration = narration;
		this.stkValue = stkValue;
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
	@Override
	public String toString() {
		return "Transactions [sNo=" + sNo + ", creditAmt=" + creditAmt + ", debitAmt=" + debitAmt + ", narration="
				+ narration + "]";
	}
	@Override
	public int compareTo(Transactions o) {
		return this.sNo.compareTo(o.sNo);
	}
	
	
}
