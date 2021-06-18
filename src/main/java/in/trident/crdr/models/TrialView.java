package in.trident.crdr.models;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 18 Jun 2021
 * @version 0.0.5b
 *
 */
public class TrialView {
	
	private String accName;
	private String debit;
	private String credit;
	
	public String getAccName() {
		return accName;
	}
	
	@Override
	public String toString() {
		return "TrialObj [accName=" + accName + ", debit=" + debit + ", credit=" + credit + "]";
	}
	public void setAccName(String accName) {
		this.accName = accName;
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
	
}
