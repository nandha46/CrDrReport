package in.trident.crdr.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.util.FormatUtil;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 18 Jun 2021
 * 
 * @version 0.0.9e
 *
 */
@Service
public class TrialServiceImpl implements TrialBalService {

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private CloseBalRepo closeBalRepo;

	@Autowired
	private AccountBalanceService accountBalanceService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TrialServiceImpl.class);

	@Override
	public List<TrialView> createTrialBal(TrialForm trialform, Long uid, Long cid) {
		Profiler profiler = new Profiler("TrialBalService");
		profiler.setLogger(LOGGER);
		profiler.start("CreateTrialBal");
		LOGGER.info("Start TrialBal Service");

		List<TrialView> listTrialview = new LinkedList<>();
		List<AccHead> list = accHeadRepo.findAllAccHead(uid, cid);
		Collections.sort(list);

		for (int i = 0; i < list.size(); i++) {
			if (i == 1) {
				TrialView cashOnHand = new TrialView();
				cashOnHand.setAccName("Cash on Hand");
				cashOnHand.setLevel(2);
				cashOnHand.setHeader(true);
				double cashVal = FormatUtil.nvlDouble(closeBalRepo.findCloseBalByDate(trialform.getEndDate(), uid, cid));
				if (cashVal < 0) {
					cashOnHand.setCredit(FormatUtil.formatAmount(Math.abs(cashVal)));
					cashOnHand.setDebit("");
				} else {
					cashOnHand.setCredit("");
					cashOnHand.setDebit(FormatUtil.formatAmount(Math.abs(cashVal)));
				}
				listTrialview.add(cashOnHand);
			}

			AccHead acc = list.get(i);
			TrialView tv = new TrialView();
			tv.setAccName(acc.getAccName());
			String[] arr = calculateTrialBalance(acc.getAccCode(), trialform.getEndDate(), uid, cid);
			if ("Cr".equals(arr[1])) {
				tv.setDebit("");
				tv.setCredit(arr[0]);
			} else {
				tv.setDebit(arr[0]);
				tv.setCredit("");
			}
			tv.setHeader(acc.getAccCode() == 0);
			tv.setLevel(acc.getLevel1());

			if (trialform.isZeroBal() && (("0.00".equals(tv.getDebit()) && tv.getCredit().isEmpty())
					|| ("0.00".equals(tv.getCredit()) && tv.getDebit().isEmpty()))) {
				continue;
			}
			listTrialview.add(tv);
		}

		BigDecimal debitTotal = BigDecimal.ZERO;
		BigDecimal creditTotal = BigDecimal.ZERO;
		for (TrialView tv : listTrialview) {
			debitTotal = debitTotal.add(FormatUtil.parseFormattedAmount(tv.getDebit()));
			creditTotal = creditTotal.add(FormatUtil.parseFormattedAmount(tv.getCredit()));
		}
		listTrialview.add(new TrialView("Total", FormatUtil.formatAmount(debitTotal),
				FormatUtil.formatAmount(creditTotal), 1, true));

		TimeInstrument ti = profiler.stop();
		ti.log();
		if (trialform.isReportOrder()) {
			return createReportGroup(listTrialview, trialform.getLevel());
		}
		return listTrialview;
	}

	@Override
	public String[] calculateTrialBalance(Integer code, String endDate, Long uid, Long cid) {
		return accountBalanceService.calculateAccHeadBalance(code, endDate, uid, cid);
	}

	@Override
	public List<TrialView> createReportGroup(List<TrialView> listTrialview, int level) {
		if (listTrialview == null || listTrialview.isEmpty()) {
			return Collections.emptyList();
		}
		List<TrialView> altList = new ArrayList<>();
		for (int i = 0; i < listTrialview.size() - 1; i++) {
			if (listTrialview.get(i + 1).getLevel() > level) {
				BigDecimal debit = BigDecimal.ZERO;
				BigDecimal credit = BigDecimal.ZERO;
				for (int j = i + 1; j < listTrialview.size(); j++) {
					debit = debit.add(FormatUtil.parseFormattedAmount(listTrialview.get(j).getDebit()));
					credit = credit.add(FormatUtil.parseFormattedAmount(listTrialview.get(j).getCredit()));
					if (j + 1 >= listTrialview.size() || listTrialview.get(j + 1).getLevel() < listTrialview.get(j).getLevel()) {
						altList.add(new TrialView(listTrialview.get(i).getAccName(), FormatUtil.formatAmount(debit),
								FormatUtil.formatAmount(credit), listTrialview.get(i).getLevel(),
								listTrialview.get(i).isHeader()));
						i = j;
						break;
					}
				}
			} else {
				altList.add(listTrialview.get(i));
			}
		}
		altList.add(listTrialview.get(listTrialview.size() - 1));
		return altList;
	}
}
