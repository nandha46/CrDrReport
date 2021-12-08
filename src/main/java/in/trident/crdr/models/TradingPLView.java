package in.trident.crdr.models;

/** 
* @author Nandhakumar Subramanian
* 
* @since 21 Jun 2021
* 
* @version 0.0.5b
*
*/

public class TradingPLView {
	
	private String particulars;
	private String debit;
	private String credit;
	private int level;
	
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	@Override
	public String toString() {
		return "TradingPLView [particulars=" + particulars + ", debit=" + debit + ", credit=" + credit + ", level="
				+ level + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + ((particulars == null) ? 0 : particulars.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradingPLView other = (TradingPLView) obj;
		if (level != other.level)
			return false;
		if (particulars == null) {
			if (other.particulars != null)
				return false;
		} else if (!particulars.equals(other.particulars))
			return false;
		return true;
	}
	
	
	
}
