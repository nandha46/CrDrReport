package in.trident.crdr.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.TplBalView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.services.AccountBalanceService.BalanceResult;
import in.trident.crdr.util.FormatUtil;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 26 Jun 2021
 * 
 * @version 0.0.9e
 *
 */
@Service
public class BalSheetServiceImpl implements BalanceSheetService {

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private TradingPLService tradingPLService;

	@Autowired
	private CloseBalRepo closeBalrepo;

	@Autowired
	private AccountBalanceService accountBalanceService;

	private static final Logger LOGGER = LoggerFactory.getLogger(BalSheetServiceImpl.class);

	@Override
	public List<BalanceSheetView> createBalSheet(CommonForm balSheetForm, Long uid, Long cid) {
		Profiler profiler = new Profiler("BalSheetServiceImpl");
		profiler.setLogger(LOGGER);
		profiler.start("Balance Sheet");

		List<BalanceSheetView> listBalSheet = new LinkedList<>();
		List<TradingPLView> tradingPLViews = tradingPLService.createTradingPL(balSheetForm, uid, cid);

		LocalDate end = FormatUtil.parseIsoDate(balSheetForm.getEndDate());
		String fyStart = accountBalanceService.resolveFinancialYearStart(uid, end).format(FormatUtil.ISO_DATE);
		Map<Integer, BigDecimal> deltaMap = accountBalanceService.loadNetDeltasByAccCode(fyStart,
				balSheetForm.getEndDate(), uid, cid);

		List<Schedule> liabilityList = scheduleRepo.findAllLiabilityAccounts(uid, cid);
		boolean netProfitInserted = false;
		for (int i = 0; i < liabilityList.size(); i++) {
			if (i == 1 && !netProfitInserted) {
				addNetProfitIfPresent(listBalSheet, tradingPLViews);
				netProfitInserted = true;
			}

			Schedule acc = liabilityList.get(i);
			BalanceSheetView balSheetView = toBalanceSheetView(acc, deltaMap);
			if (!isZeroRowSkipped(balSheetForm, balSheetView)) {
				listBalSheet.add(balSheetView);
			}
		}
		if (!netProfitInserted) {
			addNetProfitIfPresent(listBalSheet, tradingPLViews);
		}

		List<Schedule> assetList = scheduleRepo.findAllAssetAccounts(uid, cid);
		boolean assetExtrasInserted = false;
		for (int i = 0; i < assetList.size(); i++) {
			if (i == 1 && !assetExtrasInserted) {
				addAssetExtras(listBalSheet, balSheetForm, tradingPLViews, uid, cid);
				assetExtrasInserted = true;
			}

			Schedule acc = assetList.get(i);
			BalanceSheetView balSheetView = toBalanceSheetView(acc, deltaMap);
			if (!isZeroRowSkipped(balSheetForm, balSheetView)) {
				listBalSheet.add(balSheetView);
			}
		}
		if (!assetExtrasInserted) {
			addAssetExtras(listBalSheet, balSheetForm, tradingPLViews, uid, cid);
		}

		BalanceSheetView closingStock = new BalanceSheetView();
		closingStock.setLevel1(2);
		closingStock.setParticulars("Closing Stock");
		closingStock.setCredit("");
		closingStock.setHeader(true);
		closingStock.setDebit(balSheetForm.getClosingStock() == null ? "" : FormatUtil.formatAmount(balSheetForm.getClosingStock()));
		listBalSheet.add(closingStock);

		BigDecimal tdebit = BigDecimal.ZERO;
		BigDecimal tcredit = BigDecimal.ZERO;
		for (BalanceSheetView row : listBalSheet) {
			tdebit = tdebit.add(FormatUtil.parseFormattedAmount(row.getDebit()));
			tcredit = tcredit.add(FormatUtil.parseFormattedAmount(row.getCredit()));
		}

		BalanceSheetView total = new BalanceSheetView();
		total.setLevel1(1);
		total.setHeader(true);
		total.setParticulars("Total");
		total.setCredit(FormatUtil.formatAmount(tcredit));
		total.setDebit(FormatUtil.formatAmount(tdebit));
		listBalSheet.add(total);

		List<BalanceSheetView> listBalsheetViews = createReportGroup(listBalSheet, balSheetForm.getLevel());
		TimeInstrument ti = profiler.stop();
		ti.log();
		return listBalsheetViews;
	}

	private void addNetProfitIfPresent(List<BalanceSheetView> listBalSheet, List<TradingPLView> tradingPLViews) {
		TradingPLView netProfitTplview = findTradingPlRow(tradingPLViews, "Net Profit");
		if (netProfitTplview != null) {
			BalanceSheetView netProfitB4 = new BalanceSheetView();
			netProfitB4.setLevel1(2);
			netProfitB4.setHeader(true);
			netProfitB4.setParticulars("Net Profit B/f");
			netProfitB4.setCredit(netProfitTplview.getDebit());
			netProfitB4.setDebit(netProfitTplview.getCredit());
			listBalSheet.add(netProfitB4);
		}
	}

	private void addAssetExtras(List<BalanceSheetView> listBalSheet, CommonForm balSheetForm,
			List<TradingPLView> tradingPLViews, Long uid, Long cid) {
		double cashOnHand = accountBalanceService.resolveCashOnHand(balSheetForm.getEndDate(), uid, cid);
		if (!(balSheetForm.isZeroBal() && cashOnHand == 0.0)) {
			BalanceSheetView cash = new BalanceSheetView();
			cash.setLevel1(2);
			cash.setHeader(true);
			cash.setParticulars("Cash on Hand");
			if (cashOnHand < 0) {
				cash.setDebit("");
				cash.setCredit(FormatUtil.formatAmount(Math.abs(cashOnHand)));
			} else {
				cash.setDebit(FormatUtil.formatAmount(cashOnHand));
				cash.setCredit("");
			}
			listBalSheet.add(cash);
		}

		TradingPLView netLossTplview = findTradingPlRow(tradingPLViews, "Net Loss");
		if (netLossTplview != null) {
			BalanceSheetView netLossB4 = new BalanceSheetView();
			netLossB4.setLevel1(2);
			netLossB4.setHeader(true);
			netLossB4.setParticulars("Net Loss B/f");
			netLossB4.setCredit(netLossTplview.getDebit());
			netLossB4.setDebit(netLossTplview.getCredit());
			listBalSheet.add(netLossB4);
		}
	}

	private TradingPLView findTradingPlRow(List<TradingPLView> rows, String particulars) {
		if (rows == null) {
			return null;
		}
		for (TradingPLView row : rows) {
			if (particulars.equals(row.getParticulars())) {
				return row;
			}
		}
		return null;
	}

	private BalanceSheetView toBalanceSheetView(Schedule acc, Map<Integer, BigDecimal> deltaMap) {
		BalanceSheetView balSheetView = new BalanceSheetView();
		balSheetView.setParticulars(acc.getAccName());
		balSheetView.setHeader(acc.getAccCode() == 0);
		balSheetView.setLevel1(acc.getLevel1());
		if (acc.getAccCode() == 0) {
			balSheetView.setDebit("");
			balSheetView.setCredit("");
			return balSheetView;
		}
		double openCr = FormatUtil.nvlDouble(acc.getCrAmt());
		double openDr = FormatUtil.nvlDouble(acc.getDrAmt());
		BalanceResult bal = accountBalanceService.resolveFromOpeningAndDeltaMap(openCr, openDr, acc.getAccCode(),
				deltaMap);
		if ("Cr".equals(bal.getSide())) {
			balSheetView.setDebit("");
			balSheetView.setCredit(bal.getFormattedAmount());
		} else {
			balSheetView.setDebit(bal.getFormattedAmount());
			balSheetView.setCredit("");
		}
		return balSheetView;
	}

	private BalanceSheetView toBalanceSheetView(Schedule acc, String endDate, Long uid, Long cid) {
		BalanceSheetView balSheetView = new BalanceSheetView();
		balSheetView.setParticulars(acc.getAccName());
		balSheetView.setLevel1(acc.getLevel1());
		balSheetView.setHeader(acc.getAccCode() == 0);
		String[] arr = calculateLedgerBalance(acc.getAccCode(), endDate, uid, cid);
		if ("Cr".equals(arr[1])) {
			balSheetView.setDebit("");
			balSheetView.setCredit(arr[0]);
		} else {
			balSheetView.setDebit(arr[0]);
			balSheetView.setCredit("");
		}
		return balSheetView;
	}

	private boolean isZeroRowSkipped(CommonForm balSheetForm, BalanceSheetView balSheetView) {
		return balSheetForm.isZeroBal()
				&& (("0.00".equals(balSheetView.getDebit()) && balSheetView.getCredit().isEmpty())
						|| ("0.00".equals(balSheetView.getCredit()) && balSheetView.getDebit().isEmpty()));
	}

	@Override
	public String[] calculateLedgerBalance(Integer code, String endDate, Long uid, Long cid) {
		return accountBalanceService.calculateScheduleBalance(code, endDate, uid, cid);
	}

	@Override
	public List<List<TplBalView>> createBalSheet2(CommonForm balSheetForm, Long uid, Long cid) {
		List<BalanceSheetView> balview = createBalSheet(balSheetForm, uid, cid);
		List<TplBalView> asset = new LinkedList<>();
		List<TplBalView> liability = new LinkedList<>();
		for (BalanceSheetView bv : balview) {
			if (bv.getDebit().isEmpty() && bv.getCredit().isEmpty()) {
				continue;
			}
			if (!bv.getDebit().isEmpty() && !bv.getCredit().isEmpty()) {
				asset.add(new TplBalView(bv.getParticulars(), bv.getDebit(), bv.getLevel1(), bv.isHeader()));
				liability.add(new TplBalView(bv.getParticulars(), bv.getCredit(), bv.getLevel1(), bv.isHeader()));
			} else if (bv.getDebit().isEmpty()) {
				liability.add(new TplBalView(bv.getParticulars(), bv.getCredit(), bv.getLevel1(), bv.isHeader()));
			} else {
				asset.add(new TplBalView(bv.getParticulars(), bv.getDebit(), bv.getLevel1(), bv.isHeader()));
			}
		}
		List<List<TplBalView>> list = new LinkedList<>();
		list.add(liability);
		list.add(asset);
		return list;
	}

	@Override
	public List<BalanceSheetView> createReportGroup(List<BalanceSheetView> list, int level) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<BalanceSheetView> trimmedList = new ArrayList<>();
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i + 1).getLevel1() > level) {
				BigDecimal debit = BigDecimal.ZERO;
				BigDecimal credit = BigDecimal.ZERO;
				for (int j = i + 1; j < list.size(); j++) {
					debit = debit.add(FormatUtil.parseFormattedAmount(list.get(j).getDebit()));
					credit = credit.add(FormatUtil.parseFormattedAmount(list.get(j).getCredit()));
					if (j + 1 >= list.size() || list.get(j + 1).getLevel1() < list.get(j).getLevel1()) {
						trimmedList.add(new BalanceSheetView(list.get(i).getParticulars(), FormatUtil.formatAmount(debit),
								FormatUtil.formatAmount(credit), list.get(i).getLevel1(), list.get(i).isHeader()));
						i = j;
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
