package in.trident.crdr.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CloseBal;
import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.CompanyRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.CompanyService;

/**
 * Seeds a multi-year fictional demo company ("Aurora Horizon Trading Pvt. Ltd.")
 * for the default developer user on non-test profiles when not yet present.
 */
@Component
@Profile("!test")
public class DemoDataSeeder implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataSeeder.class);
	private static final String DEMO_COMPANY = "Aurora Horizon Trading Pvt. Ltd.";

	private final UserRepository userRepo;
	private final CompanyRepo companyRepo;
	private final AccHeadRepo accHeadRepo;
	private final DaybookRepository daybookRepo;
	private final CloseBalRepo closeBalRepo;
	private final ScheduleRepo scheduleRepo;
	private final CompSelectionRepo compSelectionRepo;
	private final CompanyService companyService;

	public DemoDataSeeder(UserRepository userRepo, CompanyRepo companyRepo, AccHeadRepo accHeadRepo,
			DaybookRepository daybookRepo, CloseBalRepo closeBalRepo, ScheduleRepo scheduleRepo,
			CompSelectionRepo compSelectionRepo, CompanyService companyService) {
		this.userRepo = userRepo;
		this.companyRepo = companyRepo;
		this.accHeadRepo = accHeadRepo;
		this.daybookRepo = daybookRepo;
		this.closeBalRepo = closeBalRepo;
		this.scheduleRepo = scheduleRepo;
		this.compSelectionRepo = compSelectionRepo;
		this.companyService = companyService;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		User demoUser = userRepo.findByEmail("nandha@mail.com");
		if (demoUser == null) {
			return;
		}
		Long uid = demoUser.getId();
		boolean alreadySeeded = companyRepo.findCompaniesByUser(uid).stream()
				.anyMatch(c -> DEMO_COMPANY.equals(c.getCompName()));
		if (alreadySeeded) {
			return;
		}

		LOGGER.info("Seeding multi-year demo data for '{}' (userId={})", DEMO_COMPANY, uid);

		Company fy22 = seedYear(uid, 2022, "2022-23", 120000.0, 185000.0, 1.0);
		Company fy23 = seedYear(uid, 2023, "2023-24", 165000.0, 230000.0, 1.24);
		Company fy24 = seedYear(uid, 2024, "2024-25", 215000.0, 295000.0, 1.52);

		if (compSelectionRepo.findCompanyByUser(uid) == null) {
			companyService.storeSelection(uid, fy24.getCompanyid());
		}
		LOGGER.info("Seeded 3 financial years for '{}' (ids={}, {}, {})", DEMO_COMPANY, fy22.getCompanyid(),
				fy23.getCompanyid(), fy24.getCompanyid());
	}

	private Company seedYear(Long uid, int startYear, String compYear, double openCash, double closeStk,
			double scale) {
		Company c = new Company();
		c.setUserid(uid);
		c.setCompName(DEMO_COMPANY);
		c.setCompYear(compYear);
		c.setCompType("T");
		c.setAddress1("42 Horizon Tech Park");
		c.setCity("Bengaluru");
		c.setState("Karnataka");
		c.setFromDate(LocalDateTime.of(startYear, 4, 1, 0, 0));
		c.setToDate(LocalDateTime.of(startYear + 1, 3, 31, 23, 59));
		c.setOpenCash(openCash);
		c.setCloseStk(closeStk);
		c.setcNoofAc(10);
		c.setcNoofEntries(24);
		c = companyRepo.save(c);
		Long cid = c.getCompanyid();

		double bankOpen = Math.round(240000.0 * scale);
		double arOpen = Math.round(95000.0 * scale);
		double apOpen = Math.round(62000.0 * scale);
		double capitalOpen = openCash + bankOpen + arOpen - apOpen;

		List<AccHead> heads = new ArrayList<>();
		heads.add(new AccHead(101, "Cash in Hand", "A", 0.0, 0.0, 1, 1, 10, "CASH", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(102, "Horizon Current Bank", "A", bankOpen, 0.0, 2, 1, 20, "BANK", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(103, "Accounts Receivable", "A", arOpen, 0.0, 3, 1, 30, "AR", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(201, "Share Capital", "L", 0.0, capitalOpen, 4, 1, 40, "CAP", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(202, "Accounts Payable", "L", 0.0, apOpen, 5, 1, 50, "AP", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(401, "Product Sales Revenue", "I", 0.0, 0.0, 6, 3, 60, "SALE", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(402, "Consulting Income", "I", 0.0, 0.0, 7, 3, 70, "CONS", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(501, "Cost of Goods Sold", "E", 0.0, 0.0, 8, 3, 80, "COGS", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(502, "Office Rent & Ops", "E", 0.0, 0.0, 9, 5, 90, "RENT", 0, 0, true, false, 0, "N", uid, cid));
		heads.add(new AccHead(503, "Payroll & Benefits", "E", 0.0, 0.0, 10, 5, 100, "PAY", 0, 0, true, false, 0, "N", uid, cid));
		accHeadRepo.saveAll(heads);

		List<Schedule> schedules = new ArrayList<>();
		schedules.add(new Schedule(102, "Horizon Current Bank", "A", bankOpen, 0.0, 2, 20, uid, cid));
		schedules.add(new Schedule(103, "Accounts Receivable", "A", arOpen, 0.0, 2, 30, uid, cid));
		schedules.add(new Schedule(201, "Share Capital", "L", 0.0, capitalOpen, 2, 40, uid, cid));
		schedules.add(new Schedule(202, "Accounts Payable", "L", 0.0, apOpen, 2, 50, uid, cid));
		scheduleRepo.saveAll(schedules);

		List<Daybook> entries = new ArrayList<>();
		List<CloseBal> closeBals = new ArrayList<>();
		closeBals.add(new CloseBal(String.format("%04d-04-01", startYear), 0.0, 0.0, openCash, uid, cid));
		int sno = 1;
		for (int m = 0; m < 12; m++) {
			int month = ((3 + m) % 12) + 1;
			int yr = month >= 4 ? startYear : startYear + 1;
			String d1 = String.format("%04d-%02d-08", yr, month);
			String d2 = String.format("%04d-%02d-22", yr, month);

			double sale = Math.round((82000.0 + m * 6500.0) * scale);
			double expense = Math.round((46000.0 + m * 2800.0) * scale);

			entries.add(new Daybook(sno++, d1, "Invoice Settlement - Enterprise Client", 401, 0.0, sale, 0, uid, cid));
			entries.add(new Daybook(sno++, d1, "Bank Receipt - Enterprise Client", 102, sale, 0.0, 0, uid, cid));
			entries.add(new Daybook(sno++, d2, "Monthly Payroll, Cloud & Supplier Payout", 503, expense, 0.0, 0, uid, cid));
			entries.add(new Daybook(sno++, d2, "Bank Payment - Monthly Payroll & Ops", 102, 0.0, expense, 0, uid, cid));
		}
		daybookRepo.saveAll(entries);
		closeBalRepo.saveAll(closeBals);

		return c;
	}
}
