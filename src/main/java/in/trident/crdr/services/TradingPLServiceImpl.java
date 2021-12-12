package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedHashSet;
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

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.5b
 * @since 21 Jun 2021
 *
 */
@Service
public class TradingPLServiceImpl implements TradingPLService {

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(TradingPLServiceImpl.class);

	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	private static int counter = 0;

	private Double netCredit = 0d;
	private Double netDebit = 0d;

	@Override
	public List<TradingPLView> createTradingPL(CommonForm tradingPLForm, Long uid, Long cid) {
		Profiler profiler = new Profiler("TradingPLService");
		profiler.setLogger(LOGGER);
		profiler.start("Start TradingPL Service");
		netCredit = 0d;
		netDebit = 0d;
		LinkedHashSet<TradingPLView> tradingPLViewSet = new LinkedHashSet<>();
		counter = 0;
		if (tradingPLForm.isReportOrder()) { // Group == true

		} else {
			// All - false
			List<AccHead> tradingAccs = accHeadRepo.findTradingPLAccs(uid, cid);
			Collections.sort(tradingAccs);
			tradingAccs.forEach((accs) -> {
				if (accs.getOrderCode() == 3 || accs.getOrderCode() == 4) {
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					String[] arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), uid, cid);
					if (arr[1].equals("Cr")) {
						tplv.setDebit("");
						tplv.setCredit(arr[0]);
					} else {
						tplv.setDebit(arr[0]);
						tplv.setCredit("");
					}
					tplv.setLevel(accs.getLevel1());
					if (tradingPLForm.isZeroBal() && ((tplv.getDebit().equals("0.00") && tplv.getCredit().isEmpty())
							|| (tplv.getCredit().equals("0.00") && tplv.getDebit().isEmpty()))) {
						// Intentionally left empty to remove ZeroBal accounts
					} else {
						tradingPLViewSet.add(tplv);
					}

				} else {
					String[] arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), uid, cid);
					if (counter < 1) {
						// Closing Stock
						TradingPLView closingStock = new TradingPLView();
						closingStock.setParticulars("Clsoing Stock");
						closingStock.setLevel(1);
						closingStock.setDebit("");
						if (tradingPLForm.getClosingStock() == null) {
							closingStock.setCredit(nf.format(0.0d).toString());
						} else {
							closingStock.setCredit(nf.format(tradingPLForm.getClosingStock()).toString());
						}
						tradingPLViewSet.add(closingStock);
						// Gross Profit
						TradingPLView grossProfit = new TradingPLView();
						Double debitTotal = tradingPLViewSet.stream().filter(x -> !x.getDebit().isEmpty())
								.mapToDouble(x -> Double.parseDouble(x.getDebit().replace(",", ""))).sum();
						Double creditTotal = tradingPLViewSet.stream().filter(x -> !x.getCredit().isEmpty())
								.mapToDouble(x -> Double.parseDouble(x.getCredit().replace(",", ""))).sum();
						Double gp = debitTotal - creditTotal;
						if (gp > 0d) {
							grossProfit.setDebit("");
							grossProfit.setCredit(nf.format(Math.abs(gp)).toString());
							grossProfit.setParticulars("Gross Loss");
						} else {
							grossProfit.setDebit(nf.format(Math.abs(gp)).toString());
							grossProfit.setCredit("");
							grossProfit.setParticulars("Gross Profit");
						}
						grossProfit.setLevel(0);
						tradingPLViewSet.add(grossProfit);
						// Total
						TradingPLView total = new TradingPLView();
						total.setParticulars("Total");
						debitTotal = tradingPLViewSet.stream().filter(x -> !x.getDebit().isEmpty())
								.mapToDouble(x -> Double.parseDouble(x.getDebit().replace(",", ""))).sum();
						creditTotal = tradingPLViewSet.stream().filter(x -> !x.getCredit().isEmpty())
								.mapToDouble(x -> Double.parseDouble(x.getCredit().replace(",", ""))).sum();
						total.setDebit(nf.format(debitTotal).toString());
						total.setCredit(nf.format(creditTotal).toString());
						total.setLevel(0);
						tradingPLViewSet.add(total);
						// Gross Profit B/f
						TradingPLView grossProfitB = new TradingPLView();
						if (gp > 0d) {
							grossProfitB.setDebit(nf.format(Math.abs(gp)).toString());
							grossProfitB.setCredit("");
							grossProfitB.setParticulars("Gross Loss B/f");
							netDebit += Math.abs(gp);
						} else {
							grossProfitB.setDebit("");
							grossProfitB.setCredit(nf.format(Math.abs(gp)).toString());
							grossProfitB.setParticulars("Gross Profit B/f");
							netCredit += Math.abs(gp);
						}
						grossProfitB.setLevel(0);
						tradingPLViewSet.add(grossProfitB);
						counter++;
					}
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), uid, cid);
					if (arr[1].equals("Cr")) {
						tplv.setDebit("");
						tplv.setCredit(arr[0]);
						if (arr[0].equals("")) {
							// Intentionally left empty to remove empty string
						} else {
							netCredit += Double.parseDouble(arr[0].replace(",", ""));
						}
					} else {
						tplv.setDebit(arr[0]);
						tplv.setCredit("");
						if (arr[0].equals("")) {
							// Intentionally left empty to remove empty string
						} else {
							netDebit += Double.parseDouble(arr[0].replace(",", ""));
						}
					}
					tplv.setLevel(accs.getLevel1());
					if (tradingPLForm.isZeroBal() && ((tplv.getDebit().equals("0.00") && tplv.getCredit().isEmpty())
							|| (tplv.getCredit().equals("0.00") && tplv.getDebit().isEmpty()))) {
						// Intentionally left empty to remove ZeroBal accounts
					} else {
						tradingPLViewSet.add(tplv);
					}
				}

			});
		}
		// Net Profit or Loss
		TradingPLView netProfit = new TradingPLView();
		Double netprofitvalue = netDebit - netCredit;
		if (netprofitvalue > 0) {
			netProfit.setCredit(nf.format(Math.abs(netprofitvalue)).toString());
			netProfit.setDebit("");
			netProfit.setParticulars("Net Loss");
			netCredit += Math.abs(netprofitvalue);
			} else {
			netProfit.setCredit("");
			netProfit.setDebit(nf.format(Math.abs(netprofitvalue)).toString());
			netDebit += Math.abs(netprofitvalue);
			netProfit.setParticulars("Net Profit");
		}
		netProfit.setLevel(0);
		tradingPLViewSet.add(netProfit);
		
		// Total
		TradingPLView total2 = new TradingPLView();
		total2.setParticulars("Total.");
		total2.setCredit(nf.format(netCredit).toString());
		total2.setDebit(nf.format(netDebit).toString());
		total2.setLevel(0);
		tradingPLViewSet.add(total2);
		List<TradingPLView> listTradingPLView = new LinkedList<TradingPLView>(tradingPLViewSet);
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		ti.log();
		return listTradingPLView;
	}

	@Override
	public String[] calculateTradingBalance(Integer code, String endDate, Long uid, Long cid) {
		LOGGER.debug("Start of CalculateTradingBalance method");
		String[] arr = { "", "" }; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = { "", "Cr" };
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code, uid, cid);
		Double d2 = accHeadRepo.findDrAmt(code, uid, cid);

		if (d1 == 0d) {
			// Prev year Bal is Dr
			LOGGER.debug("AccCode" + code + "Opening Debit: " + d2);
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate, uid, cid);
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
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate, uid, cid);
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
		LOGGER.debug("End of CalculateTradingPL method");
		return arr;

	}

}
