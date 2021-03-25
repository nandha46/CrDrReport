package in.trident.crdr.entities;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class LedgerBalance {
	
	private String date;
	private long crTot;
	private long drTot;
	private long closeBl;
	
	public DaybookBalance findBalance(ArrayList<Daybook> daybookList) {
		
		for (Daybook d : daybookList) {
			crTot += d.getCrAmt();
			drTot += d.getDrAmt();
			date = d.getDate();
		}
		closeBl = crTot - drTot;
		DaybookBalance dBal = new DaybookBalance();
		dBal.setCloseBl(closeBl);
		dBal.setCrTot(crTot);
		dBal.setDrTot(drTot);
		dBal.setDate(date);
		return dBal;
	}
	
	@Override
	public String toString() {
		return "DaybookBalance [date=" + date + ", crTot=" + crTot + ", drTot=" + drTot + ", closeBl=" + closeBl + "]";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getCrTot() {
		return crTot;
	}
	public void setCrTot(long crTot) {
		this.crTot = crTot;
	}
	public long getDrTot() {
		return drTot;
	}
	public void setDrTot(long drTot) {
		this.drTot = drTot;
	}
	public long getCloseBl() {
		return closeBl;
	}
	public void setCloseBl(long closeBl) {
		this.closeBl = closeBl;
	}

}
