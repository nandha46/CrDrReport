package in.trident.crdr;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import in.trident.crdr.repositories.CloseBalRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CloseBalTests {

	@Autowired
	private CloseBalRepo closeBalRepo;
	
	@Test
	public void testGetCloseBal() {
		System.out.println("-----------Close Balance Test Start----------");
		
		String date = "2020-04-01";
		Double closeBal = closeBalRepo.findCloseBalByDate(date);
		System.out.println(closeBal);
		
		System.out.println("-----------Close Balance Test End----------");
	}
	
	@Test
	public void testDay() {
		System.out.println(closeBalRepo.findDateDiff("2020-04-01","2020-05-08"));
	}
	
}