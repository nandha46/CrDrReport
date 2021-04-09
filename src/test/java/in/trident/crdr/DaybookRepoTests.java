package in.trident.crdr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

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
		int days = daybookRepo.findDaysBetween("2021-03-30","2020-04-01" );
		System.out.println("Days : "+days);
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
			listOflist.add(daybookList);
			calender.add(Calendar.DATE, 1);
		}
		
		System.out.println(listOflist.isEmpty());
		for (ArrayList<Daybook> list : listOflist ) {
			System.out.println(list.isEmpty());
			DaybookBalance daybookBalance = new DaybookBalance();
			DaybookBalance dbbal = daybookBalance.findBalance(list);
			System.out.println("Closing Balance :" + dbbal.getCloseBl());	
		}
	/*	listOflist.forEach(list -> {
		DaybookBalance dbbal = new DaybookBalance().findBalance(list);
		System.out.println("Closing Balance :" + dbbal.getCloseBl());	
		}); */
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
	}
}
