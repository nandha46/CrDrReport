/**
 * 
 */
package in.trident.crdr.models;

import java.util.List;

/**
 * @author Nandhakumar Subramanian
 *
 * @since 0.0.5
 */
public class LedgerView {
	private String accheadName;
	private String OpeningBal;
	private String date;
	List<AccountHeads> listAccHeads;
	
	public String getAccheadName() {
		return accheadName;
	}
	public void setAccheadName(String accheadName) {
		this.accheadName = accheadName;
	}
	public String getOpeningBal() {
		return OpeningBal;
	}
	public void setOpeningBal(String openingBal) {
		OpeningBal = openingBal;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<AccountHeads> getListAccHeads() {
		return listAccHeads;
	}
	public void setListAccHeads(List<AccountHeads> listAccHeads) {
		this.listAccHeads = listAccHeads;
	}
	@Override
	public String toString() {
		return "LedgerView [accheadName=" + accheadName + ", OpeningBal=" + OpeningBal + ", date=" + date
				+ ", listAccHeads=" + listAccHeads + "]";
	}
	
}
