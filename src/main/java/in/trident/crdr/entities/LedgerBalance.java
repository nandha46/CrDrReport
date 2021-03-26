package in.trident.crdr.entities;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class LedgerBalance {
	
	private String date;
	private Double crTot;
	private Double drTot;
	private Double closeBl;
	
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
		crTot =0d;
		drTot=0d;
		closeBl = 0d;
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
	public Double getCrTot() {
		return crTot;
	}
	public void setCrTot(Double crTot) {
		this.crTot = crTot;
	}
	public Double getDrTot() {
		return drTot;
	}
	public void setDrTot(Double drTot) {
		this.drTot = drTot;
	}
	public Double getCloseBl() {
		return closeBl;
	}
	public void setCloseBl(Double closeBl) {
		this.closeBl = closeBl;
	}

}
