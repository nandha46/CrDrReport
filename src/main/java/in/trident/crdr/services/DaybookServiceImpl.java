package in.trident.crdr.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.annotations.TrackExecutionTime;
import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CloseBal;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.Transactions;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.util.FormatUtil;

/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @version 0.0.9e
 */
@Service
public class DaybookServiceImpl implements DaybookService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DaybookServiceImpl.class);

	@Autowired
	private CloseBalRepo closeBalRepo;

	@Autowired
	private DaybookRepository dbRepo;

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private AccountBalanceService accountBalanceService;

	@TrackExecutionTime
	@Override
	public List<DaybookView> daybookViewRange(String startDate, String endDate, Long userid, Long cid) {
		LocalDate start = FormatUtil.parseIsoDate(startDate);
		LocalDate end = FormatUtil.parseIsoDate(endDate);
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("End date must not be before start date.");
		}

		String startIso = start.format(FormatUtil.ISO_DATE);
		String endIso = end.format(FormatUtil.ISO_DATE);
		List<Daybook> rangeEntries = dbRepo.findDaybookRange(startIso, endIso, userid, cid);
		if (rangeEntries == null || rangeEntries.isEmpty()) {
			return Collections.emptyList();
		}

		Map<String, List<Daybook>> byDate = new TreeMap<>();
		for (Daybook d : rangeEntries) {
			String iso = FormatUtil.parseIsoDate(d.getDate()).format(FormatUtil.ISO_DATE);
			byDate.computeIfAbsent(iso, k -> new ArrayList<>()).add(d);
		}

		Map<String, CloseBal> closeBalByDate = new HashMap<>();
		List<CloseBal> closeBals = closeBalRepo.findCloseBalList(start.minusDays(1).format(FormatUtil.ISO_DATE), endIso,
				userid, cid);
		if (closeBals != null) {
			for (CloseBal cb : closeBals) {
				if (cb.getDate() != null) {
					closeBalByDate.put(FormatUtil.parseIsoDate(cb.getDate()).format(FormatUtil.ISO_DATE), cb);
				}
			}
		}

		Map<Integer, String> shortNames = new HashMap<>();
		List<AccHead> accHeads = accHeadRepo.findAllAccHead(userid, cid);
		if (accHeads != null) {
			for (AccHead ah : accHeads) {
				shortNames.put(ah.getAccCode(), ah.getShortName());
			}
		}

		List<DaybookView> daybooks = new LinkedList<>();
		double runningClose = accountBalanceService.resolveCashOnHand(start.minusDays(1).format(FormatUtil.ISO_DATE),
				userid, cid);

		for (Map.Entry<String, List<Daybook>> entry : byDate.entrySet()) {
			String isoDate = entry.getKey();
			LocalDate parsedDate = FormatUtil.parseIsoDate(isoDate);
			List<Daybook> dayEntries = entry.getValue();

			DaybookView daybookView = new DaybookView();
			daybookView.setDate(FormatUtil.formatDisplayDate(isoDate));
			daybookView.setDayOfWeek(parsedDate.getDayOfWeek().name());

			CloseBal prevCb = closeBalByDate.get(parsedDate.minusDays(1).format(FormatUtil.ISO_DATE));
			double prevClose = prevCb != null ? prevCb.getCloseBal() : runningClose;
			CloseBal currCb = closeBalByDate.get(isoDate);
			double debitTotal = currCb != null ? currCb.getDrTot() : 0.0;
			double creditTotal = currCb != null ? currCb.getCrTot() : 0.0;
			double closingBal = currCb != null ? currCb.getCloseBal() : prevClose;
			runningClose = closingBal;

			if (prevClose >= 0) {
				daybookView.setDebitTot(FormatUtil.formatAmount(debitTotal + prevClose));
				daybookView.setCreditTot(FormatUtil.formatAmount(creditTotal));
			} else {
				daybookView.setDebitTot(FormatUtil.formatAmount(debitTotal));
				daybookView.setCreditTot(FormatUtil.formatAmount(creditTotal + Math.abs(prevClose)));
			}
			daybookView.setClosingBal(FormatUtil.formatAmount(closingBal));

			List<Transactions> trans = new ArrayList<>(dayEntries.size());
			for (Daybook transaction : dayEntries) {
				String shortName = shortNames.get(transaction.getAcccode());
				Transactions t = new Transactions(transaction.getsNo(), transaction.getCrAmt(), transaction.getDrAmt(),
						transaction.getNarration(), transaction.getSktValue(), shortName);
				t.setId(transaction.getDbId());
				t.setAcccode(transaction.getAcccode());
				t.setDate(isoDate);
				trans.add(t);
			}
			Collections.sort(trans);
			daybookView.setTransList(trans);
			daybooks.add(daybookView);
		}
		return daybooks;
	}

	@Override
	public DaybookView createDaybook(String date, int day, Long uid, Long cid) {
		LocalDate parsedDate = FormatUtil.parseIsoDate(date);
		String isoDate = parsedDate.format(FormatUtil.ISO_DATE);
		ArrayList<Daybook> daybook = dbRepo.findDaybookByDate(isoDate, uid, cid);
		if (daybook.isEmpty()) {
			return null;
		}

		DaybookView daybookView = new DaybookView();
		Daybook firstEntry = daybook.get(0);
		daybookView.setDate(FormatUtil.formatDisplayDate(firstEntry.getDate()));
		daybookView.setDayOfWeek(parsedDate.getDayOfWeek().name());

		String prevDate = parsedDate.minusDays(1).format(FormatUtil.ISO_DATE);
		double prevClose = accountBalanceService.resolveCashOnHand(prevDate, uid, cid);
		double debitTotal = FormatUtil.nvlDouble(closeBalRepo.findDebitTotal(isoDate, uid, cid));
		double creditTotal = FormatUtil.nvlDouble(closeBalRepo.findCreditTotal(isoDate, uid, cid));
		double closingBal = accountBalanceService.resolveCashOnHand(isoDate, uid, cid);

		if (prevClose >= 0) {
			daybookView.setDebitTot(FormatUtil.formatAmount(debitTotal + prevClose));
			daybookView.setCreditTot(FormatUtil.formatAmount(creditTotal));
		} else {
			daybookView.setDebitTot(FormatUtil.formatAmount(debitTotal));
			daybookView.setCreditTot(FormatUtil.formatAmount(creditTotal + Math.abs(prevClose)));
		}
		daybookView.setClosingBal(FormatUtil.formatAmount(closingBal));

		List<Transactions> trans = new ArrayList<>();
		for (Daybook transaction : daybook) {
			String shortName = accHeadRepo.findShortNameByAccHead(transaction.getAcccode(), uid, cid);
			Transactions t = new Transactions(transaction.getsNo(), transaction.getCrAmt(),
					transaction.getDrAmt(), transaction.getNarration(), transaction.getSktValue(), shortName);
			t.setId(transaction.getDbId());
			t.setAcccode(transaction.getAcccode());
			t.setDate(isoDate);
			trans.add(t);
		}
		Collections.sort(trans);
		daybookView.setTransList(trans);
		return daybookView;
	}
}
