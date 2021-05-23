package in.trident.crdr.entities;

public class Transactions {

	private int sNo;
	private Double creditAmt;
	private Double debitAmt;
	private String narration;
	private int stkValue;
	
	public int getsNo() {
		return sNo;
	}
	public void setsNo(int sNo) {
		this.sNo = sNo;
	}
	public Double getCreditAmt() {
		return creditAmt;
	}
	public void setCreditAmt(Double creditAmt) {
		this.creditAmt = creditAmt;
	}
	public Double getDebitAmt() {
		return debitAmt;
	}
	public void setDebitAmt(Double debitAmt) {
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
	
	
}
