package in.trident.crdr.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.TplBalView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.services.AccountBalanceService.BalanceResult;
import in.trident.crdr.util.FormatUtil;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9e
 * @since 21 Jun 2021
 *
 */
@Service
public class TradingPLServiceImpl implements TradingPLService {

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private AccountBalanceService accountBalanceService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TradingPLServiceImpl.class);

	@Override
	public List<TradingPLView> createTradingPL(CommonForm tradingPLForm, Long uid, Long cid) {
		Profiler profiler = new Profiler("TradingPLService");
		profiler.setLogger(LOGGER);
		profiler.start("Start TradingPL Service");

		BigDecimal netCredit = BigDecimal.ZERO;
		BigDecimal netDebit = BigDecimal.ZERO;
		BigDecimal tradingDebit = BigDecimal.ZERO;
		BigDecimal tradingCredit = BigDecimal.ZERO;
		LinkedHashSet<TradingPLView> tradingPLViewSet = new LinkedHashSet<>();
		boolean grossProfitInserted = false;

		List<AccHead> tradingAccs = accHeadRepo.findTradingPLAccs(uid, cid);
		Collections.sort(tradingAccs);

		LocalDate end = FormatUtil.parseIsoDate(tradingPLForm.getEndDate());
		String fyStart = accountBalanceService.resolveFinancialYearStart(uid, end).format(FormatUtil.ISO_DATE);
		Map<Integer, BigDecimal> deltaMap = accountBalanceService.loadNetDeltasByAccCode(fyStart,
				tradingPLForm.getEndDate(), uid, cid);

		for (AccHead accs : tradingAccs) {
			if (accs.getOrderCode() == 3 || accs.getOrderCode() == 4) {
				TradingPLView tplv = new TradingPLView();
				tplv.setParticulars(accs.getAccName());
				tplv.setHeader(accs.getAccCode() == 0);
				BalanceResult bal = accountBalanceService.resolveFromOpeningAndDeltaMap(accs.getCrAmt(),
						accs.getDrAmt(), accs.getAccCode(), deltaMap);
				if (accs.getAccCode() == 0) {
					tplv.setDebit("");
					tplv.setCredit("");
				} else if ("Cr".equals(bal.getSide())) {
					tplv.setDebit("");
					tplv.setCredit(bal.getFormattedAmount());
				} else {
					tplv.setDebit(bal.getFormattedAmount());
					tplv.setCredit("");
				}
				tplv.setLevel(accs.getLevel1());
				if (!(tradingPLForm.isZeroBal() && (("0.00".equals(tplv.getDebit()) && tplv.getCredit().isEmpty())
						|| ("0.00".equals(tplv.getCredit()) && tplv.getDebit().isEmpty())))) {
					if (accs.getAccCode() != 0) {
						if ("Cr".equals(bal.getSide())) {
							tradingCredit = tradingCredit.add(bal.getAmount());
						} else {
							tradingDebit = tradingDebit.add(bal.getAmount());
						}
					}
					tradingPLViewSet.add(tplv);
				}
			} else {
				if (!grossProfitInserted) {
					BigDecimal closeStk = BigDecimal.valueOf(FormatUtil.nvlDouble(tradingPLForm.getClosingStock()));
					TradingPLView closingStock = new TradingPLView();
					closingStock.setParticulars("Closing Stock");
					closingStock.setHeader(true);
					closingStock.setLevel(1);
					closingStock.setDebit("");
					closingStock.setCredit(FormatUtil.formatAmount(closeStk));
					tradingPLViewSet.add(closingStock);
					tradingCredit = tradingCredit.add(closeStk);

					BigDecimal gp = tradingDebit.subtract(tradingCredit);
					TradingPLView grossProfit = new TradingPLView();
					if (gp.compareTo(BigDecimal.ZERO) > 0) {
						grossProfit.setDebit("");
						grossProfit.setCredit(FormatUtil.formatAmount(gp.abs()));
						grossProfit.setParticulars("Gross Loss");
						tradingCredit = tradingCredit.add(gp.abs());
					} else {
						grossProfit.setDebit(FormatUtil.formatAmount(gp.abs()));
						grossProfit.setCredit("");
						grossProfit.setParticulars("Gross Profit");
						tradingDebit = tradingDebit.add(gp.abs());
					}
					grossProfit.setLevel(1);
					grossProfit.setHeader(true);
					tradingPLViewSet.add(grossProfit);

					TradingPLView total = new TradingPLView();
					total.setParticulars("Total");
					total.setDebit(FormatUtil.formatAmount(tradingDebit));
					total.setCredit(FormatUtil.formatAmount(tradingCredit));
					total.setLevel(1);
					total.setHeader(true);
					tradingPLViewSet.add(total);

					TradingPLView grossProfitB = new TradingPLView();
					if (gp.compareTo(BigDecimal.ZERO) > 0) {
						grossProfitB.setDebit(FormatUtil.formatAmount(gp.abs()));
						grossProfitB.setCredit("");
						grossProfitB.setParticulars("Gross Loss B/f");
						netDebit = netDebit.add(gp.abs());
					} else {
						grossProfitB.setDebit("");
						grossProfitB.setCredit(FormatUtil.formatAmount(gp.abs()));
						grossProfitB.setParticulars("Gross Profit B/f");
						netCredit = netCredit.add(gp.abs());
					}
					grossProfitB.setLevel(1);
					grossProfitB.setHeader(true);
					tradingPLViewSet.add(grossProfitB);
					grossProfitInserted = true;
				}

				TradingPLView tplv = new TradingPLView();
				tplv.setParticulars(accs.getAccName());
				tplv.setHeader(accs.getAccCode() == 0);
				BalanceResult bal = accountBalanceService.resolveFromOpeningAndDeltaMap(accs.getCrAmt(),
						accs.getDrAmt(), accs.getAccCode(), deltaMap);
				if (accs.getAccCode() == 0) {
					tplv.setDebit("");
					tplv.setCredit("");
				} else if ("Cr".equals(bal.getSide())) {
					tplv.setDebit("");
					tplv.setCredit(bal.getFormattedAmount());
					netCredit = netCredit.add(bal.getAmount());
				} else {
					tplv.setDebit(bal.getFormattedAmount());
					tplv.setCredit("");
					netDebit = netDebit.add(bal.getAmount());
				}
				tplv.setLevel(accs.getLevel1());
				if (!(tradingPLForm.isZeroBal() && (("0.00".equals(tplv.getDebit()) && tplv.getCredit().isEmpty())
						|| ("0.00".equals(tplv.getCredit()) && tplv.getDebit().isEmpty())))) {
					tradingPLViewSet.add(tplv);
				}
			}
		}

		BigDecimal netProfitValue = netDebit.subtract(netCredit);
		TradingPLView netProfit = new TradingPLView();
		if (netProfitValue.compareTo(BigDecimal.ZERO) > 0) {
			netProfit.setCredit(FormatUtil.formatAmount(netProfitValue.abs()));
			netProfit.setDebit("");
			netProfit.setParticulars("Net Loss");
			netCredit = netCredit.add(netProfitValue.abs());
		} else {
			netProfit.setCredit("");
			netProfit.setDebit(FormatUtil.formatAmount(netProfitValue.abs()));
			netDebit = netDebit.add(netProfitValue.abs());
			netProfit.setParticulars("Net Profit");
		}
		netProfit.setLevel(2);
		netProfit.setHeader(true);
		tradingPLViewSet.add(netProfit);

		TradingPLView total2 = new TradingPLView();
		total2.setParticulars("Total.");
		total2.setCredit(FormatUtil.formatAmount(netCredit));
		total2.setDebit(FormatUtil.formatAmount(netDebit));
		total2.setLevel(1);
		total2.setHeader(true);
		tradingPLViewSet.add(total2);

		List<TradingPLView> listTradingPLView = createReportGroup(new LinkedList<>(tradingPLViewSet),
				tradingPLForm.getLevel());
		TimeInstrument ti = profiler.stop();
		ti.log();
		return listTradingPLView;
	}

	@Override
	public String[] calculateTradingBalance(Integer code, String endDate, Long uid, Long cid) {
		return accountBalanceService.calculateAccHeadBalance(code, endDate, uid, cid);
	}

	@Override
	public List<List<TplBalView>> createTradingPL2(CommonForm tradingPLForm, Long uid, Long cid) {
		List<TradingPLView> tradingplview = createTradingPL(tradingPLForm, uid, cid);
		List<TplBalView> expense = new LinkedList<>();
		List<TplBalView> income = new LinkedList<>();
		for (TradingPLView tplv : tradingplview) {
			if (tplv.getDebit().isEmpty() && tplv.getCredit().isEmpty()) {
				continue;
			}
			if (!tplv.getDebit().isEmpty() && !tplv.getCredit().isEmpty()) {
				expense.add(new TplBalView(tplv.getParticulars(), tplv.getDebit(), tplv.getLevel(), tplv.isHeader()));
				income.add(new TplBalView(tplv.getParticulars(), tplv.getCredit(), tplv.getLevel(), tplv.isHeader()));
			} else if (tplv.getDebit().isEmpty()) {
				income.add(new TplBalView(tplv.getParticulars(), tplv.getCredit(), tplv.getLevel(), tplv.isHeader()));
			} else {
				expense.add(new TplBalView(tplv.getParticulars(), tplv.getDebit(), tplv.getLevel(), tplv.isHeader()));
			}
		}
		List<List<TplBalView>> list = new LinkedList<>();
		list.add(expense);
		list.add(income);
		return list;
	}

	@Override
	public List<TradingPLView> createReportGroup(List<TradingPLView> list, int level) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}
		List<TradingPLView> altList = new ArrayList<>();
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i + 1).getLevel() > level) {
				BigDecimal debit = BigDecimal.ZERO;
				BigDecimal credit = BigDecimal.ZERO;
				for (int j = i + 1; j < list.size(); j++) {
					debit = debit.add(FormatUtil.parseFormattedAmount(list.get(j).getDebit()));
					credit = credit.add(FormatUtil.parseFormattedAmount(list.get(j).getCredit()));
					if (j + 1 >= list.size() || list.get(j + 1).getLevel() < list.get(j).getLevel()) {
						altList.add(new TradingPLView(list.get(i).getParticulars(), FormatUtil.formatAmount(debit),
								FormatUtil.formatAmount(credit), list.get(i).getLevel(), list.get(i).isHeader()));
						i = j;
						break;
					}
				}
			} else {
				altList.add(list.get(i));
			}
		}
		altList.add(list.get(list.size() - 1));
		return altList;
	}
}
