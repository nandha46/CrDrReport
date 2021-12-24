package in.trident.crdr.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.TplBalView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
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

	@Autowired
	private TradingPLService tradingPLService;

	@Autowired
	private CloseBalRepo closeBalrepo;

	@Autowired
	private CompSelectionRepo csr;

	private static final Logger LOGGER = LoggerFactory.getLogger(BalSheetServiceImpl.class);

	private static int counter = 0;

	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public List<BalanceSheetView> createBalSheet(CommonForm balSheetForm, Long uid, Long cid) {
		Profiler profiler = new Profiler("BalSheetServiceImpl");
		profiler.setLogger(LOGGER);
		profiler.start("Balance Sheet");
		List<BalanceSheetView> listBalSheet = new LinkedList<BalanceSheetView>();
		counter = 0;
		List<Schedule> liabilitylist = scheduleRepo.findAllLiabilityAccounts(uid, cid);
		liabilitylist.forEach((acc) -> {
			if (counter == 1) {
				// Net Profit B/f from TradingPL
				TradingPLView test = new TradingPLView();
				test.setLevel(1);
				test.setParticulars("Net Profit");
				List<TradingPLView> listTPL = tradingPLService.createTradingPL(balSheetForm, uid, cid);
				int index = listTPL.indexOf(test);
				if (index != -1) {
					TradingPLView netProfitTplview = listTPL.get(index);
					BalanceSheetView netProfitB4 = new BalanceSheetView();
					netProfitB4.setLevel1(2);
					netProfitB4.setHeader(true);
					netProfitB4.setParticulars("Net Profit B/f");
					netProfitB4.setCredit(netProfitTplview.getDebit());
					netProfitB4.setDebit(netProfitTplview.getCredit());
					listBalSheet.add(netProfitB4);
				}
				counter++;

			}
			if (counter == 0) {
				// skiping first iteration
				counter++;
			}
			BalanceSheetView balSheetView = new BalanceSheetView();
			balSheetView.setParticulars(acc.getAccName());
			balSheetView.setLevel1(acc.getLevel1());
			if(acc.getAccCode() == 0) {
				balSheetView.setHeader(true);
			} else {
				balSheetView.setHeader(false);
			}
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
		List<Schedule> assetList = scheduleRepo.findAllAssetAccounts(uid, cid);
		assetList.forEach((acc) -> {
			if (counter == 3) {
				// add cash on hand by closebalRepo
				BalanceSheetView cash = new BalanceSheetView();
				cash.setLevel1(2);
				cash.setHeader(true);
				cash.setParticulars("Cash on Hand");
				Double cashOnHand = closeBalrepo.findCloseBalByDate(balSheetForm.getEndDate(), uid, cid);
				cash.setDebit(nf.format(cashOnHand).toString());
				cash.setCredit("");
				listBalSheet.add(cash);
				counter++;
				// Net Profit B/f from TradingPL
				TradingPLView test2 = new TradingPLView();
				test2.setLevel(2);
				test2.setParticulars("Net Loss");
				List<TradingPLView> listTPL = tradingPLService.createTradingPL(balSheetForm, uid, cid);
				int index = listTPL.indexOf(test2);
				if (index != -1) {
					TradingPLView netProfitTplview = listTPL.get(index);
					BalanceSheetView netProfitB4 = new BalanceSheetView();
					netProfitB4.setLevel1(2);
					netProfitB4.setHeader(true);
					netProfitB4.setParticulars("Net Loss B/f");
					netProfitB4.setCredit(netProfitTplview.getDebit());
					netProfitB4.setDebit(netProfitTplview.getCredit());
					listBalSheet.add(netProfitB4);
				}

			}

			if (counter == 2) {
				// skipping first iteration
				counter++;
			}
			BalanceSheetView balSheetView = new BalanceSheetView();
			balSheetView.setParticulars(acc.getAccName());
			balSheetView.setLevel1(acc.getLevel1());
			if(acc.getAccCode() == 0) {
				balSheetView.setHeader(true);
			} else {
				balSheetView.setHeader(false);
			}
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
		// Closing stock
		BalanceSheetView closingStock = new BalanceSheetView();
		closingStock.setLevel1(2);
		closingStock.setParticulars("Closing Stock");
		closingStock.setCredit("");
		closingStock.setHeader(true);
		if (balSheetForm.getClosingStock() == null) {
			closingStock.setDebit("");
		} else {
			closingStock.setDebit(nf.format(balSheetForm.getClosingStock()).toString());
		}
		listBalSheet.add(closingStock);
		// Total
		BalanceSheetView total = new BalanceSheetView();
		total.setLevel1(1);
		total.setHeader(true);
		total.setParticulars("Total");
		Double tdebit = listBalSheet.stream().filter(x -> !x.getDebit().isEmpty())
				.mapToDouble(x -> Double.parseDouble(x.getDebit().replace(",", ""))).sum();
		Double tcredit = listBalSheet.stream().filter(x -> !x.getCredit().isEmpty())
				.mapToDouble(x -> Double.parseDouble(x.getCredit().replace(",", ""))).sum();
		total.setCredit(nf.format(tcredit).toString());
		total.setDebit(nf.format(tdebit).toString());
		listBalSheet.add(total);
		List<BalanceSheetView> listBalsheetViews = createReportGroup(listBalSheet, balSheetForm.getLevel());
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		ti.log();
		return listBalsheetViews;
	}

	@Override
	public String[] calculateLedgerBalance(Integer code, String endDate, Long uid, Long cid) {
		LOGGER.debug("Start of CalculateLedgerBalance method");
		CompanySelection cs = csr.findCompanyByUser(uid);
		String startDate = cs.getFromDate().format(dateFormat);
		String[] arr = { "", "" }; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = { "", "Cr" };
			return array;
		}
		// ----------------------------
		Double d1 = scheduleRepo.findCrAmt(code, uid, cid);
		Double d2 = scheduleRepo.findDrAmt(code, uid, cid);

		if (d1 == 0d) {
			// Prev year Bal is Dr
			LOGGER.debug("AccCode" + code + "Opening Debit: " + d2);
			Double tmp = daybookRepo.openBal(code, startDate, endDate, uid, cid);
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
			Double tmp = daybookRepo.openBal(code, startDate, endDate, uid, cid);
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

	@Override
	public List<List<TplBalView>> createBalSheet2(CommonForm balSheetForm, Long uid, Long cid) {
		List<BalanceSheetView> balview = createBalSheet(balSheetForm, uid, cid);
		List<TplBalView> asset = new LinkedList<>();
		List<TplBalView> liability = new LinkedList<>();
		balview.forEach(bv -> {
			if (bv.getDebit().equals("") && bv.getCredit().equals("")) {
				// Intentionally left empty to remove header
			} else if (!bv.getDebit().equals("") && !bv.getCredit().equals("")) {
				asset.add(new TplBalView(bv.getParticulars(), bv.getDebit(), bv.getLevel1(),bv.isHeader()));
				liability.add(new TplBalView(bv.getParticulars(), bv.getCredit(), bv.getLevel1(),bv.isHeader()));
			} else if (bv.getDebit().equals("")) {
				// liability
				liability.add(new TplBalView(bv.getParticulars(), bv.getCredit(), bv.getLevel1(),bv.isHeader()));
			} else {
				// asset
				asset.add(new TplBalView(bv.getParticulars(), bv.getDebit(), bv.getLevel1(),bv.isHeader()));
			}
		});

		List<List<TplBalView>> list = new LinkedList<>();
		list.add(liability);
		list.add(asset);
		return list;
	}

	@Override
	public List<BalanceSheetView> createReportGroup(List<BalanceSheetView> list, int level) {
		List<BalanceSheetView> trimmedList = new ArrayList<>();
		for (int i = 0; i < list.size() - 1; i++) {
			Double debit = 0.0d;
			Double credit = 0.0d;
			String d, c;
			if (list.get(i + 1).getLevel1() > level) {
				// LOGGER.info("Head name: "+listTrialview.get(i).getAccName());

				for (int j = i + 1; j < list.size(); j++) {
					// LOGGER.info("Consumed: "+ listTrialview.get(j));
					d = list.get(j).getDebit().replace(",", "");
					c = list.get(j).getCredit().replace(",", "");
					if (!d.isEmpty())
						debit += Double.parseDouble(d);

					if (!c.isEmpty())
						credit += Double.parseDouble(c);
					// LOGGER.info("Debit: "+debit+" Credit: "+credit);
					if (list.get(j + 1).getLevel1() < list.get(j).getLevel1()) {
						trimmedList.add(new BalanceSheetView(list.get(i).getParticulars(), nf.format(debit).toString(),
								nf.format(credit).toString(), list.get(i).getLevel1(),list.get(i).isHeader()));
						i = j;
						// LOGGER.info("Break out of loop");
						break;
					}
				}

			} else {
				trimmedList.add(list.get(i));
			}
		}
		trimmedList.add(list.get(list.size() - 1));
		return trimmedList;
	}

}
