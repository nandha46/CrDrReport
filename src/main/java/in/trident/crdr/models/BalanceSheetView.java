package in.trident.crdr.models;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 26 Jun 2021
 * 
 * @version 0.0.5b
 *
 */

public class BalanceSheetView {
	
	private String particulars;
	private String debit;
	private String credit;
	
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public String getDebit() {
		return debit;
	}
	public void setDebit(String debit) {
		this.debit = debit;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	
	@Override
	public String toString() {
		return "BalanceSheetView [particulars=" + particulars + ", debit=" + debit + ", credit=" + credit + "]";
	}	
}
