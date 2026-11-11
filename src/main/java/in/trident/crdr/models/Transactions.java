package in.trident.crdr.models;

import java.math.BigDecimal;

import in.trident.crdr.util.FormatUtil;

/**
 * @author Nandhakumar Subramanian
 */
public class Transactions implements Comparable<Transactions> {

	private Integer sNo;
	private String creditAmt;
	private String debitAmt;
	private String narration;
	private int stkValue;
	private String shortName;

	public Transactions() {
	}

	public Transactions(Integer sNo, Double creditAmt, Double debitAmt, String narration, int stkValue,
			String shortName) {
		this.sNo = sNo;
		BigDecimal cr = FormatUtil.nvl(creditAmt);
		BigDecimal dr = FormatUtil.nvl(debitAmt);
		this.creditAmt = cr.compareTo(BigDecimal.ZERO) == 0 ? "" : FormatUtil.formatAmount(cr);
		this.debitAmt = dr.compareTo(BigDecimal.ZERO) == 0 ? "" : FormatUtil.formatAmount(dr);
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
