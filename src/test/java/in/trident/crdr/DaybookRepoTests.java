package in.trident.crdr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

import in.trident.crdr.models.DaybookView;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.DaybookServiceImpl;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class DaybookRepoTests {

	@Autowired
	private DaybookRepository daybookRepo;

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
	
	public void testDateFormat() throws ParseException {
	String date = "2020-04-24 00:00:00"; 
	SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
	Date d = sdf.parse(date);
	sdf.applyPattern("dd-MM-yyyy");
	date = sdf.format(d);
	System.out.println(date);
	}
	
	public void testFindDays() {
		System.out.println(daybookRepo.findDaysBetween("2020-04-08","2020-04-01"));
		System.out.println(daybookRepo.findDayOfWeek("2021-05-22"));
	}
	
	public void testDaybookView() {
		DaybookService dbs = new DaybookServiceImpl();
		DaybookView dbv =  dbs.createDaybook("2020-04-02",2, new Long(9));
		System.out.println(dbv.toString());
	}
	
}
