package in.trident.crdr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.util.FormatUtil;

@DataJpaTest
@ActiveProfiles("test")
public class DaybookRepoTests {

	@Autowired
	private DaybookRepository daybookRepo;

	@Test
	public void testIndianNumberAndDateFormatting() {
		assertThat(FormatUtil.formatAmount(100000.0)).isEqualTo("1,00,000.00");
		assertThat(FormatUtil.formatDisplayDate("2020-04-24 00:00:00")).isEqualTo("24-04-2020");
	}

	@Test
	public void testFindDaybookRangeAndOpenBal() {
		daybookRepo.save(new Daybook(1, "2020-04-01", "Cash Receipt", 101, 0.0, 2500.0, 0, 9L, 1L));
		daybookRepo.save(new Daybook(2, "2020-04-05", "Office Expense", 101, 500.0, 0.0, 0, 9L, 1L));

		assertThat(daybookRepo.findDaybookRange("2020-04-01", "2020-04-05", 9L, 1L)).hasSize(2);
		assertThat(daybookRepo.openBal(101, "2020-04-01", "2020-04-05", 9L, 1L)).isEqualTo(2000.0);
	}
}
