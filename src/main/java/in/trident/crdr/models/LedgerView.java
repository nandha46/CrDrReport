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
	private String openingBal;
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
		return openingBal;
	}
	public void setOpeningBal(String openingBal) {
		this.openingBal = openingBal;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getdOrC() {
		return dOrC;
	}
	public void setdOrC(String dOrC) {
		this.dOrC = dOrC;
	}
	public List<Dailybooks> getListDailybooks() {
		return listDailybooks;
	}
	public void setListDailybooks(List<Dailybooks> listDailybooks) {
		this.listDailybooks = listDailybooks;
	}
	@Override
	public String toString() {
		return "LedgerView [accheadName=" + accheadName + ", openingBal=" + openingBal + ", date=" + date + ", dOrC="
				+ dOrC + ", listDailybooks=" + listDailybooks + "]";
	}
	
}
