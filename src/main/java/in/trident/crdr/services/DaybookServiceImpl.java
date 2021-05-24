package in.trident.crdr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.ibm.icu.text.NumberFormat;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookView;
import in.trident.crdr.entities.Transactions;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;

@Service
public class DaybookServiceImpl implements DaybookService {

	//TODO Autowire both after Testing Complete
	
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
		Transactions txns = new Transactions();
		List<Transactions> trans = new ArrayList<Transactions>();
		daybook.forEach(transaction -> {
			txns.setsNo(transaction.getsNo());
			txns.setCreditAmt(transaction.getCrAmt());
			txns.setDebitAmt(transaction.getDrAmt());
			txns.setNarration(transaction.getNarration());
			txns.setStkValue(transaction.getsNo());
			trans.add(txns);
		});
		daybookView.setTransList(trans);
		return daybookView;
	}

}
