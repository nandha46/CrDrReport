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
	private Double crTot;
	private Double drTot;
	private Double closeBl;
	private String dayOfWeek; 

	public DaybookBalance findBalance(ArrayList<Daybook> daybookList) {
		System.out.println("Inside findBalance Method..");
		for (Daybook d : daybookList) {
			System.out.println("Parsing list of daybook...");
			System.out.println("calculating daybook balance value");
			this.setCrTot(d.getCrAmt());
			this.setDrTot(d.getDrAmt());
			this.setDate(d.getDate());
		}
		
		this.setCloseBl( this.getCrTot() - this.getDrTot() );
		DaybookBalance dBal = new DaybookBalance();
		System.out.println("DaybookBalance Object created");
		dBal.setCloseBl(this.getCloseBl());
		dBal.setCrTot(this.getCrTot());
		dBal.setDrTot(this.getDrTot());
		dBal.setDate(this.getDate());
		System.out.println("Setting calculated value to object ");
		Date date1 = new Date();
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
		}
		DateFormat df = new SimpleDateFormat("EEEE");
		dBal.setDayOfWeek(df.format(date1));
		System.out.println("Returing object");
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
	
	
}
