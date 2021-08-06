package in.trident.crdr.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

	// TODO Autowire both after Testing Complete
	//TODO Optimize by Date, Calender Object restructuring or IBM ICU library
	private static final Logger LOGGER = LoggerFactory.getLogger(DaybookServiceImpl.class);

	@Autowired
	private CloseBalRepo closeBalRepo;

	@Autowired
	private DaybookRepository dbRepo;

	@Autowired
	private AccHeadRepo accHeadRepo;

	/*
	 * LocalizedNumberFormatter nfr =
	 * NumberFormatter.with().precision(Precision.maxSignificantDigits(2))
	 * .unit(Currency.getInstance("INR"))
	 * .grouping(GroupingStrategy.ON_ALIGNED).locale(new Locale("en","in"));
	 * 
	 */

	@Override
	public List<DaybookView> daybookViewRange(String startDate, String endDate) {
		Profiler profiler = new Profiler("DaybookServiceImpl");
		profiler.setLogger(LOGGER);
		profiler.start("DaybookService");
		int days = dbRepo.findDaysBetween(endDate, startDate);
		if (days == 0)
			days = 1;
		LOGGER.debug("No of Days in-between: {}", days);
		LOGGER.debug("Start date:{} End Date:{}", startDate, endDate);
		Calendar calendar = Calendar.getInstance();
		List<DaybookView> daybooks = new LinkedList<DaybookView>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(df.parse(startDate));
		} catch (ParseException e) {
			LOGGER.error("Calendar Parsing Exception at DaybookServiceImpl Class");
			e.printStackTrace();
		}
		for (int i = 0; i <= days; i++) {
			DaybookView dbv = createDaybook(df.format(calendar.getTime()),calendar.get(Calendar.DAY_OF_WEEK));
			if (dbv != null) {
				daybooks.add(dbv);
			} else {
				
			}
			calendar.add(Calendar.DATE, 1);

		}
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		return daybooks;
	}

	@Override
	public DaybookView createDaybook(String date, int day) {
		// NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		Map<Integer,String> dayList = new HashMap<Integer,String>(7);
		dayList.put(1, "SUNDAY");
		dayList.put(2, "MONDAY");
		dayList.put(3, "TUESDAY");
		dayList.put(4, "WEDNESDAY");
		dayList.put(5, "THURSDAY");
		dayList.put(6, "FRIDAY");
		dayList.put(7, "SATURDAY");
		LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
				.precision(Precision.fixedFraction(2));
		ArrayList<Daybook> daybook = dbRepo.findDaybookByDate(date);
		DaybookView daybookView = new DaybookView();
		if (daybook.isEmpty()) {
			return null;
		} else {
		Daybook db = daybook.get(0);
		SimpleDateFormat insdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = new Date();
		//TODO Replace with reversed date string
		try {
			date1 = insdf.parse(db.getDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		daybookView.setDate(sdf.format(date1));
		daybookView.setDayOfWeek(dbRepo.findDayOfWeek(date));
	//	daybookView.setDayOfWeek(dayList.get(day));
		
		// TODO Needs to find a way to get Collection of closeBal,debit n credit total
		// for the date range to reduce calls to database
		daybookView.setClosingBal(nf.format(closeBalRepo.findCloseBalByDate(date)).toString());
		daybookView.setDebitTot(nf.format(closeBalRepo.findDebitTotal(date)).toString());
		daybookView.setCreditTot(nf.format(closeBalRepo.findCreditTotal(date)).toString());
		List<Transactions> trans = new ArrayList<Transactions>();
		daybook.forEach(transaction -> {
			String temp = accHeadRepo.findShortNameByAccHead(transaction.getAccCode());
			Transactions txns = new Transactions(transaction.getsNo(), transaction.getCrAmt(), transaction.getDrAmt(),
					transaction.getNarration(), transaction.getSktValue(), temp);
			trans.add(txns);
		});
		Collections.sort(trans);
		daybookView.setTransList(trans);
		}
		return daybookView;
	}

}
