package in.trident.crdr.entities;

public class Transactions implements Comparable<Transactions>{

	private Integer sNo;
	private Double creditAmt;
	private Double debitAmt;
	private String narration;
	private int stkValue;
	
	public Transactions () {
		
	}
	
	public Transactions(Integer sNo, Double creditAmt, Double debitAmt, String narration, int stkValue) {
		this.sNo = sNo;
		this.creditAmt = creditAmt;
		this.debitAmt = debitAmt;
		this.narration = narration;
		this.stkValue = stkValue;
	}
	public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
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
	@Override
	public int compareTo(Transactions o) {
		return this.sNo.compareTo(o.sNo);
	}
	
	
}
