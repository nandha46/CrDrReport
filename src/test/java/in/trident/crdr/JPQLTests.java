package in.trident.crdr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.repositories.DaybookRepository;

@DataJpaTest
@ActiveProfiles("test")
public class JPQLTests {

	@Autowired
	private DaybookRepository daybookRepo;

	@Test
	public void testFindDaybookByAccCodeAndDate() {
		daybookRepo.save(new Daybook(1, "2021-04-10", "Invoice 1", 201, 300.0, 0.0, 0, 2L, 5L));
		daybookRepo.save(new Daybook(2, "2021-05-01", "Invoice 2", 201, 200.0, 0.0, 0, 2L, 5L));
		daybookRepo.save(new Daybook(3, "2021-05-01", "Other Company", 201, 999.0, 0.0, 0, 2L, 99L));

		assertThat(daybookRepo.findDaybookByAccCodeAndDate(201, "2021-04-01", "2021-06-01", 2L, 5L))
				.hasSize(2)
				.extracting(Daybook::getNarration)
				.containsExactly("Invoice 1", "Invoice 2");
	}
}
