package in.trident.crdr.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.ibm.icu.text.NumberFormat;

@Component
public class DaybookBalance {

	private String date;
	private Double crTot;
	private Double drTot;
	private Double closeBl;
	private String dayOfWeek; 
	
	private String tCredit;
	private String tDebit;
	private String closeBalance;

	NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en","in"));
	
	public DaybookBalance findBalance(ArrayList<Daybook> daybookList) {
		String date = "";
		Double crTot = 0d;
		Double drTot = 0d;
		Double closeBl = 0d;
		for (Daybook d : daybookList) {
			crTot =+ d.getCrAmt();
			drTot =+ d.getDrAmt();
			date = d.getDate();
		}
		
		closeBl =  crTot - drTot ;
		DaybookBalance dBal = new DaybookBalance();
		dBal.setCloseBl(closeBl);
		dBal.setCrTot(crTot);
		dBal.setDrTot(drTot);
		dBal.settCredit(nf.format(crTot));
		dBal.settDebit(nf.format(drTot));
		dBal.setCloseBalance(nf.format(closeBl));
		dBal.setDate(date);
		Date date1 = new Date();
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
		}
		DateFormat df = new SimpleDateFormat("EEEE");
		dBal.setDayOfWeek(df.format(date1));
		return dBal;
	}
	
	@Override
	public String toString() {
		return "DaybookBalance [date=" + date + ", crTot=" + crTot + ", drTot=" + drTot + ", closeBl=" + closeBl + ", Day of week: "+ dayOfWeek +"]";
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

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String gettCredit() {
		return tCredit;
	}

	public void settCredit(String tCredit) {
		this.tCredit = tCredit;
	}

	public String gettDebit() {
		return tDebit;
	}

	public void settDebit(String tDebit) {
		this.tDebit = tDebit;
	}

	public String getCloseBalance() {
		return closeBalance;
	}

	public void setCloseBalance(String closeBalance) {
		this.closeBalance = closeBalance;
	}
	
	
}
