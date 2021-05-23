package in.trident.crdr.services;

import java.util.ArrayList;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.icu.text.NumberFormat;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookView;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;

public class DaybookServiceImpl implements DaybookService {

	@Autowired
	private DaybookRepository daybookRepo;
	
	@Autowired
	private DaybookView daybookView;
	
	@Autowired
	private CloseBalRepo closeBalRepo;
	
	NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en","in"));
	/*
	LocalizedNumberFormatter nfr = NumberFormatter.with().precision(Precision.maxSignificantDigits(2))
										.unit(Currency.getInstance("INR"))
											.grouping(GroupingStrategy.ON_ALIGNED).locale(new Locale("en","in"));
	
	*/
	@Override
	public DaybookView createDaybook(String date) {
		ArrayList<Daybook> daybook;
		daybook = daybookRepo.findDaybookByDate(date);
		Daybook db =  daybook.get(0);
		daybookView.setDate(db.getDate());
		daybookView.setDayOfWeek(daybookRepo.findDayOfWeek(date));
		daybookView.setClosingBal(nf.format(closeBalRepo.findCloseBalByDate(date)));
		daybookView.setDebitTot(nf.format(closeBalRepo.findDebitTotal(date)));
		daybookView.setCreditTot(nf.format(closeBalRepo.findCreditTotal(date)));
		daybook.forEach(transaction -> {});
		
		return daybookView;
	}

}
