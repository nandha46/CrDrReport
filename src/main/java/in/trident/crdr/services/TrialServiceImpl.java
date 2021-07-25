package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 18 Jun 2021
 * 
 * @version 0.0.5b
 *
 */

@Service
public class TrialServiceImpl implements TrialBalService {

	@Autowired
	private AccHeadRepo accHeadRepo;
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TradingPLServiceImpl.class);
	
	@Override
	public List<TrialView> createTrialBal(TrialForm trialform) {
		LOGGER.warn("Start of CreateTrialBal method");
		List<TrialView> listTrialview = new LinkedList<TrialView>();
		List<AccHead> list = accHeadRepo.findAllAccHead();
		Collections.sort(list);
		LOGGER.warn("AccHeads retrieved and sorted");
		if (trialform.isReportOrder()) {
			
		} else {
			list.forEach((acc)->{
				LOGGER.warn("Iteration of accHeads started");
				TrialView tv = new TrialView();
				tv.setAccName(acc.getAccName());
				String[] arr = calculateTrialBalance(acc.getAccCode(), trialform.getEndDate());
				if(arr[1].equals("Cr")) {
					tv.setDebit("");
					tv.setCredit(arr[0]);
				} else {
					tv.setDebit(arr[0]);
					tv.setCredit("");
				}
				tv.setLevel(acc.getLevel1());
				if (tv.getDebit().equals("ZeroB") && trialform.isZeroBal()) {
					tv.setDebit(tv.getDebit().replace("ZeroB", "0"));
					listTrialview.add(tv);
				} else {
					// If Debit returns ZeroB and isZeroBal is true trialview won't get added to view
					if (tv.getDebit().equals("ZeroB")) {
						
					} else {
						listTrialview.add(tv);
					 }
				}
			});
		}
		LOGGER.warn("End of CreateTrialBal method");
		return listTrialview;
	}

	@Override
	public String[] calculateTrialBalance(Integer code, String endDate) {
		LOGGER.warn("Start of CalculateTrialBalance method");
		String[] arr = {"",""}; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = {"","Cr"};
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code); 
		Double d2 = accHeadRepo.findDrAmt(code);
		
		if(d1 == 0d) { // If Dr is the Budget Amt
			LOGGER.warn("Budget is Dr");
			LOGGER.warn("Now Acc Code is :"+code.toString());
			// Null check daybook repos return value
			Double tmp = daybookRepo.openBal(code, "2020-04-01", endDate) ;
			if ( tmp == null) {
				arr[0] = "ZeroB";
				arr[1] = "Dr";
				return arr;
			}
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
		else {  // If Cr is the Budget Amt
			Double tmp = daybookRepo.openBal(code, "2020-04-01", endDate);
			LOGGER.warn("Budget is Cr");
			if ( tmp == null) {
				arr[0] = "ZeroB";
				arr[1] = "Dr";
				return arr;
			}
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
						d1 *= -1 ;
						arr[0] = d1.toString();
						arr[1] = "Cr";
					}
			}
			
		}
		//----------------------------
		LOGGER.warn("End of CalculateTrialBalance method");
		return arr;
	}

}
