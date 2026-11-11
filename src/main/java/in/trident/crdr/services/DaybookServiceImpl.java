package in.trident.crdr.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.annotations.TrackExecutionTime;
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

	@TrackExecutionTime
	@Override
	public List<DaybookView> daybookViewRange(String startDate, String endDate, Long userid, Long cid) {
		LocalDate start = FormatUtil.parseIsoDate(startDate);
		LocalDate end = FormatUtil.parseIsoDate(endDate);
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("End date must not be before start date.");
		}

		List<DaybookView> daybooks = new LinkedList<>();
		for (LocalDate current = start; !current.isAfter(end); current = current.plusDays(1)) {
			String isoDate = current.format(FormatUtil.ISO_DATE);
			DaybookView dbv = createDaybook(isoDate, current.getDayOfWeek().getValue(), userid, cid);
			if (dbv != null) {
				daybooks.add(dbv);
			} else {
				LOGGER.debug("No daybook entries found for date {}", isoDate);
			}
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
		double prevClose = FormatUtil.nvlDouble(closeBalRepo.findCloseBalByDate(prevDate, uid, cid));
		double debitTotal = FormatUtil.nvlDouble(closeBalRepo.findDebitTotal(isoDate, uid, cid));
		double creditTotal = FormatUtil.nvlDouble(closeBalRepo.findCreditTotal(isoDate, uid, cid));
		double closingBal = FormatUtil.nvlDouble(closeBalRepo.findCloseBalByDate(isoDate, uid, cid));

		if (prevClose > 0) {
			daybookView.setDebitTot(FormatUtil.formatAmount(debitTotal));
			daybookView.setCreditTot(FormatUtil.formatAmount(creditTotal + prevClose));
		} else {
			daybookView.setDebitTot(FormatUtil.formatAmount(debitTotal + Math.abs(prevClose)));
			daybookView.setCreditTot(FormatUtil.formatAmount(creditTotal));
		}
		daybookView.setClosingBal(FormatUtil.formatAmount(closingBal));

		List<Transactions> trans = new ArrayList<>();
		for (Daybook transaction : daybook) {
			String shortName = accHeadRepo.findShortNameByAccHead(transaction.getAcccode(), uid, cid);
			trans.add(new Transactions(transaction.getsNo(), transaction.getCrAmt(),
					transaction.getDrAmt(), transaction.getNarration(), transaction.getSktValue(), shortName));
		}
		Collections.sort(trans);
		daybookView.setTransList(trans);
		return daybookView;
	}
}
