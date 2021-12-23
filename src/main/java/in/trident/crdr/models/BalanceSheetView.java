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
	private int level1;
	
	public BalanceSheetView() {
		// Default constructor
	}
	
	public BalanceSheetView(String particulars, String debit, String credit, int level1) {
		this.particulars = particulars;
		this.debit = debit;
		this.credit = credit;
		this.level1 = level1;
	}
	
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
	public int getLevel1() {
		return level1;
	}
	public void setLevel1(int level1) {
		this.level1 = level1;
	}
	@Override
	public String toString() {
		return "BalanceSheetView [particulars=" + particulars + ", debit=" + debit + ", credit=" + credit + ", level1="
				+ level1 + "]\n";
	}
	

}
