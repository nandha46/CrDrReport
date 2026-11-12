package in.trident.crdr.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.annotations.TrackExecutionTime;
import in.trident.crdr.entities.AccHead;
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
		List<AccHead> allAccHeads = accHeadRepo.findAllAccHead(uid, cid);
		Map<Integer, AccHead> accByCode = new HashMap<>(allAccHeads.size() * 2);
		for (AccHead ah : allAccHeads) {
			accByCode.put(ah.getAccCode(), ah);
		}

		Set<Integer> accCodes;
		if (ledgerForm.isReportOrder()) {
			List<Integer> selected = ledgerForm.getAccCode();
			if (selected == null || selected.isEmpty()) {
				return ledgerList;
			}
			accCodes = new LinkedHashSet<>(selected);
		} else {
			Collections.sort(allAccHeads);
			accCodes = new LinkedHashSet<>();
			for (AccHead ah : allAccHeads) {
				accCodes.add(ah.getAccCode());
			}
		}
		accCodes.remove(0);
		if (accCodes.isEmpty()) {
			return ledgerList;
		}

		LocalDate start = FormatUtil.parseIsoDate(ledgerForm.getStartDate());
		LocalDate fyStart = accountBalanceService.resolveFinancialYearStart(uid, start);
		Map<Integer, BigDecimal> priorDeltas = Collections.emptyMap();
		if (start.isAfter(fyStart)) {
			String priorEnd = start.minusDays(1).format(FormatUtil.ISO_DATE);
			priorDeltas = accountBalanceService.loadNetDeltasByAccCode(fyStart.format(FormatUtil.ISO_DATE), priorEnd,
					uid, cid);
		}

		List<Daybook> periodEntries = daybookRepo.findDaybookRange(ledgerForm.getStartDate(), ledgerForm.getEndDate(), uid,
				cid);
		Map<Integer, List<Daybook>> entriesByCode = new HashMap<>();
		if (periodEntries != null) {
			for (Daybook d : periodEntries) {
				entriesByCode.computeIfAbsent(d.getAcccode(), k -> new ArrayList<>()).add(d);
			}
		}

		String displayStart = FormatUtil.formatDisplayDate(ledgerForm.getStartDate());
		for (Integer code : accCodes) {
			AccHead ah = accByCode.get(code);
			double openCr = ah != null ? ah.getCrAmt() : 0.0;
			double openDr = ah != null ? ah.getDrAmt() : 0.0;
			BalanceResult opening = accountBalanceService.resolveFromOpeningAndDeltaMap(openCr, openDr, code,
					priorDeltas);
			List<Daybook> codeEntries = entriesByCode.getOrDefault(code, Collections.emptyList());
			LedgerView ledgerView = buildLedgerView(ah != null ? ah.getAccName() : null, displayStart, opening,
					codeEntries, ledgerForm.isTransactedAccOnly());
			if (ledgerView != null) {
				ledgerList.add(ledgerView);
			}
		}
		return ledgerList;
	}

	private LedgerView buildLedgerView(String accName, String displayStart, BalanceResult opening,
			List<Daybook> daybooks, boolean transactedAccOnly) {
		if (daybooks.isEmpty() && transactedAccOnly) {
			return null;
		}
		LedgerView ledgerview = new LedgerView();
		ledgerview.setAccheadName(accName);
		ledgerview.setDate(displayStart);

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

		List<Dailybooks> dailybooklist = new LinkedList<>();
		BigDecimal runningBal = signedOpening;
		BigDecimal crTotal = "Cr".equals(opening.getSide()) ? opening.getAmount() : BigDecimal.ZERO;
		BigDecimal drTotal = "Cr".equals(opening.getSide()) ? BigDecimal.ZERO : opening.getAmount();

		for (Daybook entry : daybooks) {
			BigDecimal dr = FormatUtil.nvl(entry.getDrAmt());
			BigDecimal cr = FormatUtil.nvl(entry.getCrAmt());
			drTotal = drTotal.add(dr);
			crTotal = crTotal.add(cr);
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

		dailybooklist.add(new Dailybooks("Total", FormatUtil.formatAmount(drTotal), FormatUtil.formatAmount(crTotal)));
		Dailybooks finalTotal = dailybooklist.size() > 1
				? new Dailybooks("", dailybooklist.get(dailybooklist.size() - 2).getBalance(), "")
				: new Dailybooks("", opening.getFormattedAmount(), "");
		dailybooklist.add(finalTotal);
		ledgerview.setListDailybooks(dailybooklist);
		return ledgerview;
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
