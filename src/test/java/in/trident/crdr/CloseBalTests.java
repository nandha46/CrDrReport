package in.trident.crdr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import in.trident.crdr.entities.CloseBal;
import in.trident.crdr.repositories.CloseBalRepo;

@DataJpaTest
@ActiveProfiles("test")
public class CloseBalTests {

	@Autowired
	private CloseBalRepo closeBalRepo;

	@Test
	public void testGetCloseBal() {
		closeBalRepo.save(new CloseBal("2020-04-01", 1500.0, 500.0, 1000.0, 9L, 1L));
		Double closeBal = closeBalRepo.findCloseBalByDate("2020-04-01", 9L, 1L);
		Double debitTot = closeBalRepo.findDebitTotal("2020-04-01", 9L, 1L);
		Double creditTot = closeBalRepo.findCreditTotal("2020-04-01", 9L, 1L);

		assertThat(closeBal).isEqualTo(1000.0);
		assertThat(debitTot).isEqualTo(1500.0);
		assertThat(creditTot).isEqualTo(500.0);
	}
}
