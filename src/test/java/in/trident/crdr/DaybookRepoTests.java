package in.trident.crdr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.ibm.icu.text.NumberFormat;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookBalance;
import in.trident.crdr.repositories.DaybookRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class DaybookRepoTests {

	@Autowired
	private DaybookRepository daybookRepo;

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
			DaybookBalance dbbal = new DaybookBalance().findBalance(list);
			System.out.println("Closing Balance :" + nf.format(dbbal.getCloseBl()));
		});
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Test
	public void testNumberformat() {
		Double d = 45124853123456.78941;
		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		System.out.println(nf.format(d));
	}
	
	@Test
	public void testDateFormat() {
	String date = "2020-04-04 00:00:00";
	System.out.println(date.substring(0,date.length()-9));
	}
}
