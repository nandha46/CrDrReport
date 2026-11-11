package in.trident.crdr.services;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.annotations.TrackExecutionTime;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.models.Dailybooks;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.services.AccountBalanceService.BalanceResult;
import in.trident.crdr.util.FormatUtil;

/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @since 0.0.5
 */
@Service
public class LedgerServiceImpl implements LedgerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LedgerServiceImpl.class);

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private AccountBalanceService accountBalanceService;

	@TrackExecutionTime
	@Override
	public List<LedgerView> createLedgerViewList(LedgerForm ledgerForm, Long uid, Long cid) {
		List<LedgerView> ledgerList = new LinkedList<>();
		Set<Integer> accCodes;
		if (ledgerForm.isReportOrder()) {
			List<Integer> selected = ledgerForm.getAccCode();
			if (selected == null || selected.isEmpty()) {
				return ledgerList;
			}
			accCodes = new LinkedHashSet<>(selected);
		} else {
			accCodes = new LinkedHashSet<>(accHeadRepo.findAccCodes(uid, cid));
		}
		accCodes.remove(0);

		for (Integer code : accCodes) {
			LedgerView ledgerView = createLedgerView(code, ledgerForm, uid, cid);
			if (ledgerView == null) {
				LOGGER.trace("--Ledger is Empty for code {}--", code);
			} else {
				ledgerList.add(ledgerView);
			}
		}
		return ledgerList;
	}

	@Override
	public LedgerView createLedgerView(Integer code, LedgerForm ledgerForm, Long uid, Long cid) {
		LedgerView ledgerview = new LedgerView();
		ledgerview.setAccheadName(accHeadRepo.findAccNameByAccCode(code, uid, cid));
		LOGGER.debug("Code: {}", code);

		BalanceResult opening = accountBalanceService.calculateAccHeadOpeningBalance(code, ledgerForm.getStartDate(),
				uid, cid);
		ledgerview.setDate(FormatUtil.formatDisplayDate(ledgerForm.getStartDate()));

		BigDecimal signedOpening;
		if ("Cr".equals(opening.getSide())) {
			ledgerview.setdOrC(opening.getFormattedAmount());
			ledgerview.setOpeningBal("");
			signedOpening = opening.getAmount().negate();
		} else {
			ledgerview.setOpeningBal(opening.getFormattedAmount());
			ledgerview.setdOrC("");
			signedOpening = opening.getAmount();
		}

		List<Dailybooks> dailybooklist = createDailybooks(code, ledgerForm.getStartDate(), ledgerForm.getEndDate(),
				signedOpening.doubleValue(), uid, cid);
		if (dailybooklist.isEmpty() && ledgerForm.isTransactedAccOnly()) {
			LOGGER.trace("---No Transactions on Dailybooks-----");
			return null;
		}

		BigDecimal crTotal = BigDecimal.ZERO;
		BigDecimal drTotal = BigDecimal.ZERO;
		for (Dailybooks item : dailybooklist) {
			crTotal = crTotal.add(FormatUtil.parseFormattedAmount(item.getCreditAmt()));
			drTotal = drTotal.add(FormatUtil.parseFormattedAmount(item.getDebitAmt()));
		}
		if ("Cr".equals(opening.getSide())) {
			crTotal = crTotal.add(opening.getAmount());
		} else {
			drTotal = drTotal.add(opening.getAmount());
		}

		dailybooklist.add(new Dailybooks("Total", FormatUtil.formatAmount(drTotal), FormatUtil.formatAmount(crTotal)));

		Dailybooks finalTotal;
		if (dailybooklist.size() > 1) {
			String lastBal = dailybooklist.get(dailybooklist.size() - 2).getBalance();
			finalTotal = new Dailybooks("", lastBal, "");
		} else {
			finalTotal = new Dailybooks("", opening.getFormattedAmount(), "");
		}
		dailybooklist.add(finalTotal);
		ledgerview.setListDailybooks(dailybooklist);
		return ledgerview;
	}

	@Override
	public List<Dailybooks> createDailybooks(Integer code, String startDate, String endDate, Double bal, Long userid,
			Long cid) {
		List<Dailybooks> dailybooklist = new LinkedList<>();
		List<Daybook> daybooks = daybookRepo.findDaybookByAccCodeAndDate(code, startDate, endDate, userid, cid);
		BigDecimal runningBal = FormatUtil.nvl(bal);

		for (Daybook entry : daybooks) {
			BigDecimal dr = FormatUtil.nvl(entry.getDrAmt());
			BigDecimal cr = FormatUtil.nvl(entry.getCrAmt());
			if (dr.compareTo(BigDecimal.ZERO) == 0) {
				runningBal = runningBal.subtract(cr);
			} else {
				runningBal = runningBal.add(dr);
			}
			String side = runningBal.compareTo(BigDecimal.ZERO) > 0 ? "Dr" : "Cr";
			String formattedRunning = FormatUtil.formatAmount(runningBal.abs());
			dailybooklist.add(new Dailybooks(entry.getDate(), entry.getNarration(), entry.getDrAmt(), entry.getCrAmt(),
					formattedRunning, side));
		}
		return dailybooklist;
	}
}
