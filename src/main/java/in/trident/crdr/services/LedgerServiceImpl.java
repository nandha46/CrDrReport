package in.trident.crdr.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
	
	SimpleDateFormat outsdf = new SimpleDateFormat("dd-MM-yyyy");
	SimpleDateFormat insdf = new SimpleDateFormat("yyyy-MM-dd");
	NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
	
	@Override
	public List<LedgerView> createLedgerViewList(LedgerForm ledgerForm) {
		List<LedgerView> ledgerList = new LinkedList<LedgerView>();
		if(ledgerForm.isReportOrder()) {
			List<Integer> accCodes = ledgerForm.getAccCode();
			accCodes.forEach(code -> {
				LedgerView ledgerView =  createLedgerView(code,ledgerForm);
				ledgerList.add(ledgerView);
			});
		} else {
			Set<Integer> accCodes = new HashSet<Integer>(accHeadRepo.findAccCodes());
			accCodes.remove(0);
			accCodes.forEach(code -> {
				LedgerView ledgerView =  createLedgerView(code,ledgerForm);
				ledgerList.add(ledgerView);
			});
		}
		LOGGER.warn("Ledger Created");
		return ledgerList;
	}
	
	@Override
	public LedgerView createLedgerView(Integer code, LedgerForm ledgerForm) {
		LedgerView ledgerview = new LedgerView();
		ledgerview.setAccheadName(accHeadRepo.findAccNameByAccCode(code));
		String arr[] = findOpeningBal(code,ledgerForm);
 		Date date1 = new Date();
 		try {
			date1 = insdf.parse(ledgerForm.getStartDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ledgerview.setDate(outsdf.format(date1));
 		Double bal = new Double(arr[0]);
 		if(arr[1].equals("Cr")) {
 			bal *= -1;
 			ledgerview.setdOrC(arr[0]);
 		} else {
 			ledgerview.setOpeningBal(arr[0]);
 		}
 		List<Dailybooks> dailybooklist = createDailybooks(code,ledgerForm.getStartDate(),ledgerForm.getEndDate(),bal);
 		ledgerview.setListDailybooks(dailybooklist);
		return ledgerview;
	}


	@Override
	public List<Dailybooks> createDailybooks(Integer code, String startDate, String endDate, Double bal) {
		List<Dailybooks> dailybooklist = new LinkedList<Dailybooks>();
 		List<Daybook> daybooks = daybookRepo.findDaybookByAccCodeAndDate(code, startDate, endDate);
 		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
 		for(int i = 0; i<daybooks.size(); i++) {
 			String arr2[] = {"",""};
 			if(daybooks.get(i).getDrAmt() == 0) {
 				bal -= daybooks.get(i).getCrAmt();
 				if (bal > 0d) {
 					arr2[0] = nf.format(Math.abs(bal));
 					arr2[1] = "Dr";
 					
 				} else {
 					arr2[0] = nf.format(Math.abs(bal));
 					arr2[1] = "Cr";
 				}
 			} else {
 				bal += daybooks.get(i).getDrAmt(); 
 				if (bal > 0d) {
 					arr2[0] = nf.format(Math.abs(bal)); 
 					arr2[1] = "Dr";
 				} else {
 					arr2[0] = nf.format(Math.abs(bal));
 					arr2[1] = "Cr";
 				}
 			}
 			Dailybooks dailybook = new Dailybooks(daybooks.get(i).getDate(), daybooks.get(i).getNarration(), nf.format(daybooks.get(i).getDrAmt()),nf.format(daybooks.get(i).getCrAmt()),arr2[0],arr2[1]);
 			dailybooklist.add(dailybook);
 		}
		return dailybooklist;
	}

	private String[] findOpeningBal(Integer code, LedgerForm ledgerForm) {
		String arr[] = {"",""};
		if(ledgerForm.getStartDate().equals("2020-04-01")) {
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
				Double tmp = daybookRepo.openBal(code, ledgerForm.getStartDate(), ledgerForm.getEndDate());
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
