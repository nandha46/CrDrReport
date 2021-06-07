package in.trident.crdr.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.models.Dailybooks;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.repositories.AccHeadRepo;
/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @since 0.0.5
 */

@Service
public class LedgerServiceImpl implements LedgerService {
	
	@Autowired
	private AccHeadRepo accHeadRepo;
	
	@Override
	public Dailybooks createDailybooks(String date) {
		// TODO Create dailybooks for the given Acc Code
		return null;
	}

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
		ledgerview.setAccheadName(accHeadRepo.findAccName(code));
		String arr[] = findOpeningBal(code,ledgerForm);
 		ledgerview.setOpeningBal(arr[0]);
 		ledgerview.setdOrC(arr[1]);
 		ledgerview.setDate(ledgerForm.getStartDate());
 		// Loop through date range
 		List<Dailybooks> accHeadslist = new LinkedList<Dailybooks>();
 		for(int i=0;i<10;i++) {
 			String date = "";
 			accHeadslist.add(createDailybooks(date));
 		}
		return ledgerview;
	}

	private String[] findOpeningBal(Integer code, LedgerForm ledgerForm) {
		//TODO Needs an way to find if start of the Financial year
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
		}
		return arr;
	}

}
