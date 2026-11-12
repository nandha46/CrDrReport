package in.trident.crdr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CloseBal;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.services.AccountBalanceService;
import in.trident.crdr.services.AccountBalanceService.BalanceResult;
import in.trident.crdr.services.BalanceSheetService;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.LedgerService;
import in.trident.crdr.services.TradingPLService;
import in.trident.crdr.services.TrialBalService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AccountBalanceAndReportServiceTests {

	private static final Long UID = 10L;
	private static final Long CID = 20L;

	@Autowired
	private AccountBalanceService accountBalanceService;

	@Autowired
	private DaybookService daybookService;

	@Autowired
	private LedgerService ledgerService;

	@Autowired
	private TrialBalService trialBalService;

	@Autowired
	private TradingPLService tradingPLService;

	@Autowired
	private BalanceSheetService balanceSheetService;

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private CloseBalRepo closeBalRepo;

	@Autowired
	private CompSelectionRepo compSelectionRepo;

	@BeforeEach
	public void setupFixtures() {
		compSelectionRepo.save(new CompanySelection(CID, UID, "Demo Trading Co", "2020-21",
				LocalDateTime.of(2020, 4, 1, 0, 0), LocalDateTime.of(2021, 3, 31, 0, 0), "Madurai", 5, 4,
				new Date(), 500.0, 100.0, "Trading"));

		// Trial & Trading accounts
		accHeadRepo.save(new AccHead(0, "TRADING ACCOUNTS", "H", 0, 0, 1, 1, 3, "TRD", 0, 0, true, false, 0, "", UID, CID));
		accHeadRepo.save(new AccHead(301, "Purchases", "Dr", 1000, 0, 2, 2, 3, "PUR", 0, 0, true, false, 0, "", UID, CID));
		accHeadRepo.save(new AccHead(401, "Sales", "Cr", 0, 1800, 3, 2, 4, "SLS", 0, 0, true, false, 0, "", UID, CID));
		accHeadRepo.save(new AccHead(501, "Office Rent", "Dr", 200, 0, 4, 2, 5, "RNT", 0, 0, true, false, 0, "", UID, CID));
		accHeadRepo.save(new AccHead(601, "Commission Received", "Cr", 0, 100, 5, 2, 6, "COM", 0, 0, true, false, 0, "", UID, CID));

		// Balance Sheet Schedule accounts (Liabilities orderCode=2, Assets orderCode=1)
		scheduleRepo.save(new Schedule(0, "LIABILITIES", "H", 0.0, 0.0, 1, 2, UID, CID));
		scheduleRepo.save(new Schedule(201, "Capital Account", "Cr", 0.0, 5000.0, 2, 2, UID, CID));
		scheduleRepo.save(new Schedule(0, "ASSETS", "H", 0.0, 0.0, 1, 1, UID, CID));
		scheduleRepo.save(new Schedule(101, "Sundry Debtors", "Dr", 3200.0, 0.0, 2, 1, UID, CID));

		// Daybook entries
		daybookRepo.save(new Daybook(1, "2020-04-05", "Purchase goods", 301, 200.0, 0.0, 0, UID, CID));
		daybookRepo.save(new Daybook(2, "2020-04-10", "Cash sales", 401, 0.0, 400.0, 0, UID, CID));
		daybookRepo.save(new Daybook(3, "2020-04-10", "Rent paid", 501, 50.0, 0.0, 0, UID, CID));

		// Close balance on 2020-04-10 only (2020-04-05 intentionally omitted to verify null-safety)
		closeBalRepo.save(new CloseBal("2020-04-10", 400.0, 50.0, 1350.0, UID, CID));
	}

	@Test
	public void daybookGetDateIsSideEffectFree() {
		Daybook db = new Daybook(1, "2020-04-10 00:00:00", "Test", 101, 100.0, 0.0, 0, UID, CID);
		assertThat(db.getDate()).isEqualTo("2020-04-10");
		assertThat(db.getDate()).isEqualTo("2020-04-10");
		assertThat(db.getDate()).isEqualTo("2020-04-10");
	}

	@Test
	public void resolveBalanceHandlesDrCrReversals() {
		// Opening Dr 1000 + Cr 1500 net => reverses to Cr 500.00
		BalanceResult reversed = accountBalanceService.resolveBalance(BigDecimal.ZERO, new BigDecimal("1000"),
				new BigDecimal("1500"));
		assertThat(reversed.getSide()).isEqualTo("Cr");
		assertThat(reversed.getFormattedAmount()).isEqualTo("500.00");

		// Opening Cr 800 - Dr 300 net => stays Cr 500.00
		BalanceResult staysCr = accountBalanceService.resolveBalance(new BigDecimal("800"), BigDecimal.ZERO,
				new BigDecimal("-300"));
		assertThat(staysCr.getSide()).isEqualTo("Cr");
		assertThat(staysCr.getFormattedAmount()).isEqualTo("500.00");
	}

	@Test
	public void daybookRangeHandlesSingleDayAndInvalidDates() {
		List<DaybookView> singleDay = daybookService.daybookViewRange("2020-04-05", "2020-04-05", UID, CID);
		assertThat(singleDay).hasSize(1);
		assertThat(singleDay.get(0).getDate()).isEqualTo("05-04-2020");
		assertThat(singleDay.get(0).getClosingBal()).isEqualTo("0.00");

		assertThatThrownBy(() -> daybookService.daybookViewRange("2020-04-10", "2020-04-05", UID, CID))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void ledgerOpeningBalanceIncludesPriorTransactionsAndHandlesEmptySelection() {
		LedgerForm priorStartForm = new LedgerForm();
		priorStartForm.setStartDate("2020-04-08");
		priorStartForm.setEndDate("2020-04-30");
		priorStartForm.setReportOrder(true);
		priorStartForm.setAccCode(Collections.singletonList(301));
		priorStartForm.setTransactedAccOnly(false);

		List<LedgerView> views = ledgerService.createLedgerViewList(priorStartForm, UID, CID);
		assertThat(views).hasSize(1);
		// Opening Dr 1000 + prior txn Dr 200 on 2020-04-05 = 1,200.00 Dr
		assertThat(views.get(0).getOpeningBal()).isEqualTo("1,200.00");

		LedgerForm emptySelection = new LedgerForm();
		emptySelection.setStartDate("2020-04-01");
		emptySelection.setEndDate("2020-04-30");
		emptySelection.setReportOrder(true);
		assertThat(ledgerService.createLedgerViewList(emptySelection, UID, CID)).isEmpty();
	}

	@Test
	public void trialTradingAndBalanceSheetAreDeterministicAcrossRepeatedRuns() {
		TrialForm tf = new TrialForm();
		tf.setEndDate("2020-04-10");
		tf.setReportOrder(false);
		List<TrialView> trialViews = trialBalService.createTrialBal(tf, UID, CID);
		assertThat(trialViews).isNotEmpty();
		assertThat(trialViews.get(trialViews.size() - 1).getAccName()).isEqualTo("Total");

		CommonForm form = new CommonForm();
		form.setEndDate("2020-04-10");
		form.setLevel(5);
		form.setClosingStock(500.0);
		form.setReportOrder(true);

		List<TradingPLView> tpl1 = tradingPLService.createTradingPL(form, UID, CID);
		List<TradingPLView> tpl2 = tradingPLService.createTradingPL(form, UID, CID);
		assertThat(tpl1).hasSize(tpl2.size());
		assertThat(tpl1).extracting(TradingPLView::getParticulars).contains("Closing Stock", "Total.");

		List<BalanceSheetView> bs1 = balanceSheetService.createBalSheet(form, UID, CID);
		List<BalanceSheetView> bs2 = balanceSheetService.createBalSheet(form, UID, CID);
		assertThat(bs1).hasSize(bs2.size());
		assertThat(bs1).extracting(BalanceSheetView::getParticulars).contains("Cash on Hand", "Closing Stock", "Total");

		// Verify Cash on Hand resolves latest prior CloseBal on non-transaction dates,
		// and Net Profit B/f is carried forward from Trading & P&L into Balance Sheet
		CommonForm nonTxDateForm = new CommonForm();
		nonTxDateForm.setEndDate("2020-04-12");
		nonTxDateForm.setLevel(5);
		nonTxDateForm.setClosingStock(500.0);
		nonTxDateForm.setReportOrder(true);
		List<BalanceSheetView> bsNonTx = balanceSheetService.createBalSheet(nonTxDateForm, UID, CID);
		assertThat(bsNonTx).extracting(BalanceSheetView::getParticulars).contains("Net Profit B/f", "Cash on Hand");
		assertThat(accountBalanceService.resolveCashOnHand("2020-04-12", UID, CID)).isEqualTo(1350.0);
	}
}
