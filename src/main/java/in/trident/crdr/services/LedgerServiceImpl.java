package in.trident.crdr.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.NumberFormat;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.models.Dailybooks;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;
/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @since 0.0.5
 */

@Service
public class LedgerServiceImpl implements LedgerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LedgerServiceImpl.class);
	
	@Autowired
	private AccHeadRepo accHeadRepo;
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	@Override
	public List<LedgerView> createLedgerViewList(LedgerForm ledgerForm) {
		List<LedgerView> ledgerList = new LinkedList<LedgerView>();
		if(ledgerForm.isReportOrder()) {
			List<Integer> accCodes = ledgerForm.getAccCode();
			accCodes.forEach(code -> {
				LedgerView ledgerView =  createLedgerView(code,ledgerForm);
				ledgerList.add(ledgerView);
			});
		}
		return ledgerList;
	}
	
	@Override
	public LedgerView createLedgerView(Integer code, LedgerForm ledgerForm) {
		LedgerView ledgerview = new LedgerView();
		ledgerview.setAccheadName(accHeadRepo.findAccNameByAccCode(code));
		String arr[] = findOpeningBal(code,ledgerForm);
 		ledgerview.setOpeningBal(arr[0]);
 		ledgerview.setdOrC(arr[1]);
 		ledgerview.setDate(ledgerForm.getStartDate());
 		LOGGER.warn("Ledgerview string"+ledgerview.toString());
 		List<Dailybooks> dailybooklist = createDailybooks(code,ledgerForm.getStartDate(),ledgerForm.getEndDate());
 		ledgerview.setListDailybooks(dailybooklist);
 		LOGGER.warn("list set to ledgerview");
 		LOGGER.warn(ledgerview.toString());
		return ledgerview;
	}


	@Override
	public List<Dailybooks> createDailybooks(Integer code, String startDate, String endDate) {
		List<Dailybooks> dailybooklist = new LinkedList<Dailybooks>();
 		List<Daybook> daybooks = daybookRepo.findDaybookByAccCodeAndDate(code, startDate, endDate);
 		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
 		//TODO Write a method to calculate balance for Daybooks
 		// Balance value changes from Opening balance gradually
 		daybooks.forEach(db->{
 			Dailybooks dailybook = new Dailybooks(db.getDate(), db.getNarration(), nf.format(db.getDrAmt()),nf.format(db.getCrAmt()),nf.format(accHeadRepo.findDrAmt(code)),"Dr");
 			dailybooklist.add(dailybook);
 		});
		return dailybooklist;
	}

	private String[] findOpeningBal(Integer code, LedgerForm ledgerForm) {
		String arr[] = {"",""};
		if(ledgerForm.getStartDate() == "2020-04-01") {
			Double d1 = accHeadRepo.findCrAmt(code);
			Double d2 = accHeadRepo.findDrAmt(code);
			if( d1 == 0d) {
				arr[0] = d2.toString();
				arr[1] = "Dr";
			}
			else {
				arr[0] = d1.toString();
				arr[1] = "Cr";
			}
			
		} else {
			Double d1 = accHeadRepo.findCrAmt(code);
			Double d2 = accHeadRepo.findDrAmt(code);
			if( d1 == 0d) {
				Double tmp = daybookRepo.openBal(code, ledgerForm.getStartDate(), ledgerForm.getEndDate()) ;
				// If tmp is +ve Dr else Cr
				if (tmp > 0d || tmp == 0d) {
					tmp = d2 + tmp;
					arr[0] = tmp.toString();
					arr[1] = "Dr";
				} else {
					d2 = d2 + tmp;
						if (d2 > 0d) {
							arr[0]  = d2.toString();
							arr[1] = "Cr";
						} else {
							d2 *= -1;
							arr[0] = d2.toString();
							arr[1] = "Dr";
						}
				}
			}
			else {
				Double tmp = daybookRepo.openBal(code, ledgerForm.getStartDate(), ledgerForm.getEndDate()) ;
				// If tmp is +ve Cr else Dr
				if (tmp > 0d || tmp == 0d) {
					tmp = d1 + tmp;
					arr[0] = tmp.toString();
					arr[1] = "Cr";
				} else {
					d1 = d1 + tmp;
						if (d1 > 0d) {
							arr[0]  = d1.toString();
							arr[1] = "Dr";
						} else {
							d1 *= -1;
							arr[0] = d1.toString();
							arr[1] = "Cr";
						}
				}
				
			}
			
	}
		return arr;
		
	}

}
