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
 		daybooks.forEach(db->{
 			Dailybooks dailybook = new Dailybooks(db.getDate(), db.getNarration(), nf.format(db.getDrAmt()),nf.format(db.getCrAmt()),nf.format(accHeadRepo.findDrAmt(code)),"Dr");
 			dailybooklist.add(dailybook);
 		});
		return dailybooklist;
	}

	private String[] findOpeningBal(Integer code, LedgerForm ledgerForm) {
		//TODO Needs a way to find if start of the Financial year
		String arr[] = {"",""};
		if(ledgerForm.getStartDate() == "2020-04-01") {
			Double amt = accHeadRepo.findCrAmt(code);
			if (amt == 0) {
			arr[0] = accHeadRepo.findDrAmt(code).toString();
			arr[1] = "Dr";
			} else {
				arr[0] = amt.toString();
				arr[1] = "Cr";
			}
		} else {
			//TODO Logic to calculate Op Balance for middle of the year
			Double amt = accHeadRepo.findCrAmt(code);
			if (amt == 0) {
			arr[0] = accHeadRepo.findDrAmt(code).toString();
			arr[1] = "Dr ";
			} else {
				arr[0] = amt.toString();
				arr[1] = "Cr";
		}
	}
		return arr;
		
	}

}
