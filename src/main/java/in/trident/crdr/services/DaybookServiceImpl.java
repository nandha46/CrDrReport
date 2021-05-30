package in.trident.crdr.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.NumberFormat;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookView;
import in.trident.crdr.entities.Transactions;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;
/**
 * 
 * @author Nandhakumar Subramanian
 *
 */
@Service
public class DaybookServiceImpl implements DaybookService {

	//TODO Autowire both after Testing Complete
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DaybookServiceImpl.class);
	
	private CloseBalRepo closeBalRepo;
	
	private DaybookRepository dbRepo;
	
	public DaybookServiceImpl(DaybookRepository dbRepo, CloseBalRepo closeBalRepo) {
		this.dbRepo = dbRepo;
		this.closeBalRepo = closeBalRepo;
	}
	
	/*
	 * LocalizedNumberFormatter nfr =
	 * NumberFormatter.with().precision(Precision.maxSignificantDigits(2))
	 * .unit(Currency.getInstance("INR"))
	 * .grouping(GroupingStrategy.ON_ALIGNED).locale(new Locale("en","in"));
	 * 
	 */
	
	@Override
	public DaybookView createDaybook(String date) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
		ArrayList<Daybook> daybook = dbRepo.findDaybookByDate(date);
		Daybook db = daybook.get(0);
		DaybookView daybookView = new DaybookView();
		daybookView.setDate(db.getDate());
		daybookView.setDayOfWeek(dbRepo.findDayOfWeek(date));
		daybookView.setClosingBal(nf.format(closeBalRepo.findCloseBalByDate(date)));
		daybookView.setDebitTot(nf.format(closeBalRepo.findDebitTotal(date)));
		daybookView.setCreditTot(nf.format(closeBalRepo.findCreditTotal(date)));
		List<Transactions> trans = new ArrayList<Transactions>();
		daybook.forEach(transaction -> {
			Transactions txns = new Transactions(transaction.getsNo(),transaction.getCrAmt(),transaction.getDrAmt(),transaction.getNarration(),transaction.getSktValue());
			trans.add(txns);
		});
		Collections.sort(trans);
		daybookView.setTransList(trans);
		return daybookView;
	}

	@Override
	public List<DaybookView> daybookViewRange(String startDate, String endDate) {
		int days = dbRepo.findDaysBetween(endDate,startDate);
		Calendar calendar = Calendar.getInstance();
		List<DaybookView> daybooks = new LinkedList<DaybookView>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(df.parse(startDate));
		} catch (ParseException e) {
			LOGGER.error("Calendar Parsing Exception at DaybookServiceImpl Class");
			e.printStackTrace();
		}
		for(int i = 0; i<= days; i++ ) {
			
			daybooks.add(createDaybook(df.format(calendar.getTime())));
			calendar.add(Calendar.DATE, 1);
			
		}
		return daybooks;
	}

}
