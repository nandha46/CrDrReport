package in.trident.crdr.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

import in.trident.crdr.annotations.ExcecutionTimeTracker;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.Transactions;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;

/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @version 0.0.4
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

	@ExcecutionTimeTracker
	@Override
	public List<DaybookView> daybookViewRange(String startDate, String endDate, Long userid, Long cid) {
		Profiler profiler = new Profiler("DaybookServiceImpl");
		profiler.setLogger(LOGGER);
		profiler.start("DaybookService");
		int days = dbRepo.findDaysBetween(endDate, startDate);
		if (days == 0)
			days = 1;
		Calendar calendar = Calendar.getInstance();
		List<DaybookView> daybooks = new LinkedList<>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(df.parse(startDate));
		} catch (ParseException e) {
			LOGGER.error("Calendar Parsing Exception at DaybookServiceImpl Class");
			e.printStackTrace();
		}
		for (int i = 0; i <= days; i++) {
			DaybookView dbv = createDaybook(df.format(calendar.getTime()), calendar.get(Calendar.DAY_OF_WEEK), userid,
					cid);
			if (dbv != null) {
				daybooks.add(dbv);
			} else {
				LOGGER.trace("Daybook for that date is null");
			}
			calendar.add(Calendar.DATE, 1);
		}
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		return daybooks;
	}

	@Override
	public DaybookView createDaybook(String date, int day, Long uid, Long cid) {
		Map<Integer, String> dayList = new HashMap<>(7);
		dayList.put(1, "SUNDAY");
		dayList.put(2, "MONDAY");
		dayList.put(3, "TUESDAY");
		dayList.put(4, "WEDNESDAY");
		dayList.put(5, "THURSDAY");
		dayList.put(6, "FRIDAY");
		dayList.put(7, "SATURDAY");
		LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
				.precision(Precision.fixedFraction(2));
		ArrayList<Daybook> daybook = dbRepo.findDaybookByDate(date, uid, cid);
		DaybookView daybookView = new DaybookView();
		if (daybook.isEmpty()) {
			return null;
		} else {
			Daybook db = daybook.get(0);
			SimpleDateFormat insdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = new Date();
			// TODO Replace with reversed date string
			try {
				date1 = insdf.parse(db.getDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			daybookView.setDate(sdf.format(date1));
			daybookView.setDayOfWeek(dbRepo.findDayOfWeek(date));
			// daybookView.setDayOfWeek(dayList.get(day));

			Calendar ca = Calendar.getInstance();
			ca.setTime(date1);
			ca.add(Calendar.DAY_OF_MONTH, -1);
			// changes
			Double prevClose = closeBalRepo.findCloseBalByDate(insdf.format(ca.getTime()), uid, cid);
			if (prevClose > 0) {
				// add to credit
				daybookView.setDebitTot(nf.format(closeBalRepo.findDebitTotal(date, uid, cid)).toString());
				daybookView
						.setCreditTot(nf.format(closeBalRepo.findCreditTotal(date, uid, cid) + prevClose).toString());
			} else {
				daybookView.setDebitTot(
						nf.format(closeBalRepo.findDebitTotal(date, uid, cid) + Math.abs(prevClose)).toString());
				daybookView.setCreditTot(nf.format(closeBalRepo.findCreditTotal(date, uid, cid)).toString());
			}
			daybookView.setClosingBal(nf.format(closeBalRepo.findCloseBalByDate(date, uid, cid)).toString());
			List<Transactions> trans = new ArrayList<>();
			daybook.forEach(transaction -> {
				String temp = accHeadRepo.findShortNameByAccHead(transaction.getAcccode(), uid, cid);
				Transactions txns = new Transactions(transaction.getsNo(), transaction.getCrAmt(),
						transaction.getDrAmt(), transaction.getNarration(), transaction.getSktValue(), temp);
				trans.add(txns);
			});
			Collections.sort(trans);
			daybookView.setTransList(trans);

		}
		return daybookView;
	}

}
