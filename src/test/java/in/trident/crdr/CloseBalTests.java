package in.trident.crdr;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import in.trident.crdr.repositories.CloseBalRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CloseBalTests {

	@Autowired
	private CloseBalRepo closeBalRepo;
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Test
	public void testGetCloseBal() {
		System.out.println("-----------Close Balance Test Start----------");
		
		String date = "2020-04-01";
		Double closeBal = closeBalRepo.findCloseBalByDate(date);
		System.out.println(closeBal);
		
		System.out.println("-----------Close Balance Test End----------");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testDay() {
	//	List<Integer> in = jdbc.query("select datediff(?,?)",(rs,rn)-> rs.getInt(0),new Object[] {"2020-04-01","2020-05-06"});
		String num = jdbc.queryForObject("select dayname(?)", new Object[] {"2020-04-01"},String.class);
		System.out.println(num);
	}
	
}