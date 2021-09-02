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
	public List<BalanceSheetView> createBalSheet(BalSheetForm balSheetForm) {
		Profiler profiler = new Profiler("BalSheetServiceImpl");
		profiler.setLogger(LOGGER);
		profiler.start("Balance Sheet");
		List<BalanceSheetView> listBalSheet = new LinkedList<BalanceSheetView>();
		if (balSheetForm.isReportOrder()) {
			// True == Group
		} else {
			List<Schedule> list = scheduleRepo.findAll();
			Collections.sort(list);
			list.forEach((acc) -> {
				BalanceSheetView balSheetView = new BalanceSheetView();
				balSheetView.setParticulars(acc.getAccName());
				balSheetView.setLevel1(acc.getLevel1());
				String[] arr = calculateLedgerBalance(acc.getAccCode(), balSheetForm.getEndDate());
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
	public String[] calculateLedgerBalance(Integer code, String endDate) {
		LOGGER.debug("Start of CalculateLedgerBalance method");
		String[] arr = { "", "" }; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = { "", "Cr" };
			return array;
		}
		// ----------------------------
		Double d1 = scheduleRepo.findCrAmt(code);
		Double d2 = scheduleRepo.findDrAmt(code);

		if (d1 == 0d) { // If Dr is the Budget Amt
			// Null check daybook repos return value
			Double tmp = daybookRepo.openBal(code, "2020-04-01", endDate);
			if (tmp == null) {
				if (d1 == 0) {
					arr[0] = nf.format(Math.abs(d2)).toString();
					arr[1] = "Dr";
					return arr;
				} else {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Cr";
					return arr;
				}
			}
			// If tmp is +ve Dr else Cr
			if (tmp > 0d || tmp == 0d) {
				tmp = d2 + tmp;
				arr[0] = nf.format(Math.abs(tmp)).toString();
				arr[1] = "Dr";
			} else {
				d2 = d2 + tmp;
				if (d2 > 0d) {
					arr[0] = nf.format(Math.abs(d2)).toString();
					arr[1] = "Cr";
				} else {
					d2 *= -1;
					arr[0] = nf.format(Math.abs(d2)).toString();
					arr[1] = "Dr";
				}
			}
		} else { // If Cr is the Budget Amt
			Double tmp = daybookRepo.openBal(code, "2020-04-01", endDate);
			if (tmp == null) {
				if (d1 == 0) {
					arr[0] = nf.format(Math.abs(d2)).toString();
					arr[1] = "Dr";
					return arr;
				} else {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Cr";
					return arr;
				}
			}
			// If tmp is +ve Cr else Dr
			if (tmp > 0d || tmp == 0d) {
				tmp = d1 + tmp;
				arr[0] = nf.format(Math.abs(tmp)).toString();
				arr[1] = "Cr";
			} else {
				d1 = d1 + tmp;
				if (d1 > 0d) {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Dr";
				} else {
					d1 *= -1;
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Cr";
				}
			}

		}
		// ----------------------------
		LOGGER.debug("End of CalculateBalanceSheet method");
		return arr;

	}

}
