package in.trident.crdr;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import in.trident.crdr.repositories.DaybookRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class JPQLTests {
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	/*
	public void testDaybookBalance(){
		List<Object[]> obj = daybookRepo.findDaybookBalance("2020-04-30");
		obj.forEach(s -> {
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<");
			System.out.println("Date: "+s[0]); 
			System.out.println("Credit Total: "+s[1]);
			System.out.println("Debit Total: "+s[2]);
			System.out.println("Daily Balance: "+s[3]);
			System.out.println("Week Day: "+s[4]);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
			} );
	} */
	
	
	public void testFindDays() {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<");
		int days = daybookRepo.findDaysBetween("2021-06-04", "2021-04-09");
		System.out.println("Number of Days between dates : "+days);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
	}
}
