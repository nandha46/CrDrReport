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
	private String dOrC;
	
	List<Dailybooks> listDailybooks;
	
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
	public List<Dailybooks> getListAccHeads() {
		return listDailybooks;
	}
	public void setListAccHeads(List<Dailybooks> listAccHeads) {
		this.listDailybooks = listAccHeads;
	}
	public String getdOrC() {
		return dOrC;
	}
	public void setdOrC(String dOrC) {
		this.dOrC = dOrC;
	}
	@Override
	public String toString() {
		return "LedgerView [accheadName=" + accheadName + ", OpeningBal=" + OpeningBal + ", date=" + date + ", dOrC="
				+ dOrC + ", listDailybooks=" + listDailybooks + "]";
	}
	
}
