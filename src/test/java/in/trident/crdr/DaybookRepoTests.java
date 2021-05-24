package in.trident.crdr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;
import com.ibm.icu.number.NumberFormatter.GroupingStrategy;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Currency;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookBalance;
import in.trident.crdr.entities.DaybookView;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.DaybookServiceImpl;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class DaybookRepoTests {

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private CloseBalRepo closeBalRepo;
	
	@Test
	public void testDaybookBal() {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<");
		int days = daybookRepo.findDaysBetween("2020-04-08", "2020-04-01");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		try {
			calender.setTime(df.parse("2020-04-01"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ArrayList<ArrayList<Daybook>> listOflist = new ArrayList<ArrayList<Daybook>>();
		ArrayList<Daybook> daybookList;
		for (int i = 0; i <= days; i++) {
			daybookList = daybookRepo.findDaybookByDate(df.format(calender.getTime()));
			daybookList.forEach(daybook -> System.out.println(daybook.getCrAmt()));
			listOflist.add(daybookList);
			calender.add(Calendar.DATE, 1);
		}
		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en","in"));
		listOflist.forEach(list -> {
			DaybookBalance dbbal = null;
			try {
				dbbal = new DaybookBalance().findBalance(list);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Closing Balance :" + nf.format(dbbal.getCloseBl()));
		});
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Test
	public void testNumberformat() {
		Double d = 45124853123456.78941;
		Double d2 = 100000d;
		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		System.out.println(nf.format(d));
		System.out.println(nf.format(d2));
		
		LocalizedNumberFormatter nfr = NumberFormatter.with().precision(Precision.minMaxFraction(2, 2))
				.unit(Currency.getInstance("INR")).grouping(GroupingStrategy.ON_ALIGNED).locale(new Locale("en","in"));
		System.out.println(nfr.format(d).toString());
		System.out.println(nfr.format(d2).toString());
	}
	
	@Test
	public void testDateFormat() throws ParseException {
	String date = "2020-04-24 00:00:00";
	SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
	Date d = sdf.parse(date);
	sdf.applyPattern("dd-MM-yyyy");
	date = sdf.format(d);
	System.out.println(date);
	}
	
	@Test
	public void testFindDays() {
		System.out.println(daybookRepo.findDaysBetween("2020-04-08","2020-04-01"));
		System.out.println(daybookRepo.findDayOfWeek("2021-05-22"));
	}
	
	
	@Test
	public void testDaybookView() {
		DaybookService dbs = new DaybookServiceImpl(daybookRepo,closeBalRepo);
		DaybookView dbv =  dbs.createDaybook("2020-04-02");
		System.out.println(dbv.toString());
	}
	
	
	
	
}
