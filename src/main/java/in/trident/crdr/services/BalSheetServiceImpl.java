package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.BalSheetForm;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 26 Jun 2021
 * 
 * @version 0.0.5b
 *
 */
@Service
public class BalSheetServiceImpl implements BalanceSheetService {

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(BalSheetServiceImpl.class);

	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	@Override
	public List<BalanceSheetView> createBalSheet(BalSheetForm balSheetForm, Long uid, Long cid) {
		Profiler profiler = new Profiler("BalSheetServiceImpl");
		profiler.setLogger(LOGGER);
		profiler.start("Balance Sheet");
		List<BalanceSheetView> listBalSheet = new LinkedList<BalanceSheetView>();
		if (balSheetForm.isReportOrder()) {
			// True = Group
		} else {
			// false = Select All
			List<Schedule> list = scheduleRepo.findAll();
			Collections.sort(list);
			list.forEach((acc) -> {
				BalanceSheetView balSheetView = new BalanceSheetView();
				balSheetView.setParticulars(acc.getAccName());
				balSheetView.setLevel1(acc.getLevel1());
				String[] arr = calculateLedgerBalance(acc.getAccCode(), balSheetForm.getEndDate(), uid, cid);
				if (arr[1].equals("Cr")) {
					balSheetView.setDebit("");
					balSheetView.setCredit(arr[0]);
				} else {
					balSheetView.setDebit(arr[0]);
					balSheetView.setCredit("");
				}
				if (balSheetForm.isZeroBal()
						&& ((balSheetView.getDebit().equals("0.00") && balSheetView.getCredit().isEmpty())
								|| (balSheetView.getCredit().equals("0.00") && balSheetView.getDebit().isEmpty()))) {
					// Intentionally left empty to remove ZeroBal accounts
				} else {
					listBalSheet.add(balSheetView);
				}
			});
		}
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		ti.log();
		return listBalSheet;
	}

	@Override
	public String[] calculateLedgerBalance(Integer code, String endDate, Long uid, Long cid) {
		LOGGER.debug("Start of CalculateLedgerBalance method");
		String[] arr = { "", "" }; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = { "", "Cr" };
			return array;
		}
		// ----------------------------
		Double d1 = scheduleRepo.findCrAmt(code,uid,cid);
		Double d2 = scheduleRepo.findDrAmt(code,uid,cid);

		if (d1 == 0d) {
			// Prev year Bal is Dr
			LOGGER.debug("AccCode" + code + "Opening Debit: " + d2);
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate, uid,cid);
			if (tmp == null) {
				// d2 is also zero, so there is no txn & no prev year bal
				// whether d2 is 0 or Somevalue Balance is Dr
				arr[0] = nf.format(Math.abs(d2)).toString();
				arr[1] = "Dr";
				return arr;
			} else if (tmp > 0d || tmp == 0d) {
				// tmp is +ve so Cr
				tmp = d2 - tmp;
				if (tmp > 0d) {
					arr[0] = nf.format(Math.abs(tmp)).toString();
					arr[1] = "Dr";
				} else {
					arr[0] = nf.format(Math.abs(tmp)).toString();
					arr[1] = "Cr";
				}
			} else {
				d2 = d2 - tmp;
				arr[0] = nf.format(Math.abs(d2)).toString();
				arr[1] = "Dr";
			}
		} else { // then Prev year Bal is Cr
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate, uid,cid);
			if (tmp == null) {
				arr[0] = nf.format(Math.abs(d1)).toString();
				arr[1] = "Cr";
				return arr;
			} else if (tmp > 0d || tmp == 0d) {
				// tmp is +ve so Cr
				tmp = d1 + tmp;
				arr[0] = nf.format(Math.abs(tmp)).toString();
				arr[1] = "Cr";
			} else {
				// tmp is -ve so Dr
				d1 = d1 + tmp;
				if (d1 > 0d) {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Cr";
				} else {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Dr";
				}
			}

		}
		// ----------------------------
		LOGGER.debug("End of CalculateBalanceSheet method");
		return arr;

	}

}
