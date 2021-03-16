package in.trident.crdr.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;
@Component
public class DaybookBalance {

	private String date;
	private long crTot;
	private long drTot;
	private long closeBl;
	private String dayOfWeek;
	
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
		Date date1 = new Date();
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		DateFormat df = new SimpleDateFormat("EEEE");
		dayOfWeek = df.format(date1);
		dBal.setDayOfWeek(dayOfWeek);
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

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	
}
