package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TrialServiceImpl.class);
	
	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));
	
	@Override
	public List<TrialView> createTrialBal(TrialForm trialform) {
		Profiler profiler = new Profiler("TrialBalService");
		profiler.setLogger(LOGGER);
		profiler.start("CreateTrialBal");
		LOGGER.debug("Start of CreateTrialBal method");
		List<TrialView> listTrialview = new LinkedList<TrialView>();
		List<AccHead> list = accHeadRepo.findAllAccHead();
		Collections.sort(list);
		if (trialform.isReportOrder()) {
			List<Integer> accCodes = trialform.getAccCode();
			accCodes.forEach((acc)->{
				TrialView tv = new TrialView();
				tv.setAccName(accHeadRepo.findAccNameByAccCode(acc));
				String[] arr = calculateTrialBalance(acc, trialform.getEndDate());
				if(arr[1].equals("Cr")) {
					tv.setDebit("");
					tv.setCredit(arr[0]);
				} else {
					tv.setDebit(arr[0]);
					tv.setCredit("");
				}
				tv.setLevel(accHeadRepo.findLevelByAccCode(acc));
			/*	if (tv.getDebit().equals("ZeroB") && trialform.isZeroBal()) {
					tv.setDebit(tv.getDebit().replace("ZeroB", "0"));
					listTrialview.add(tv);
				} else {
					// If Debit returns ZeroB and isZeroBal is true trialview won't get added to view
					if (tv.getDebit().equals("ZeroB")) {
						
					} else {
						listTrialview.add(tv);
					 }
				} */
			});
		} else {
			list.forEach((acc)->{
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
			/*	if (tv.getDebit().equals("ZeroB") && trialform.isZeroBal()) {
					tv.setDebit(tv.getDebit().replace("ZeroB", "0"));
					listTrialview.add(tv);
				} else {
					// If Debit returns ZeroB and isZeroBal is true trialview won't get added to view
					if (tv.getDebit().equals("ZeroB")) {
						
					} else {
						listTrialview.add(tv);
					 }
				} */
			});
		}
		LOGGER.debug("End of CreateTrialBal method");
		TimeInstrument ti =  profiler.stop();
		LOGGER.info("\n" + ti.toString());
		ti.log();
		return listTrialview;
	}

	@Override
	public String[] calculateTrialBalance(Integer code, String endDate) {
		LOGGER.debug("Start of CalculateTrialBalance method");
		String[] arr = {"",""}; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = {"","Cr"};
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code); 
		Double d2 = accHeadRepo.findDrAmt(code);
		LOGGER.debug("Acc code:"+code +"- CrAmount:"+d1 +" - DrAmount:"+d2+"\n");
		if(d1 == 0d) { // If Dr is the Budget Amt
			// Null check daybook repos return value
			Double tmp = daybookRepo.openBal(code, "2020-04-01", endDate);
			if ( tmp == null) {
				if (d1 == 0) {
					arr[0] = nf.format(d2).toString();
					arr[1] = "Dr";
					return arr;
				} else {
					arr[0] = nf.format(d1).toString();
					arr[1] = "Cr";
					return arr;
				}
			}
			// If tmp is +ve Dr else Cr
			if (tmp > 0d || tmp == 0d) {
				tmp = d2 + tmp;
				arr[0] = nf.format(tmp).toString();
				arr[1] = "Dr";
			} else {
				d2 = d2 + tmp;
					if (d2 > 0d) {
						arr[0]  = nf.format(d2).toString();
						arr[1] = "Cr";
					} else {
						d2 *= -1;
						arr[0] = nf.format(d2).toString();
						arr[1] = "Dr";
					}
			}
		}
		else {  // If Cr is the Budget Amt
			Double tmp = daybookRepo.openBal(code, "2020-04-01", endDate);
			if ( tmp == null) {
				if (d1 == 0) {
					arr[0] = nf.format(d2).toString();
					arr[1] = "Dr";
					return arr;
				} else {
					arr[0] = nf.format(d1).toString();
					arr[1] = "Cr";
					return arr;
				}
			}
			// If tmp is +ve Cr else Dr
			if (tmp > 0d || tmp == 0d) {
				tmp = d1 + tmp;
				arr[0] = nf.format(tmp).toString();
				arr[1] = "Cr";
			} else {
				d1 = d1 + tmp;
					if (d1 > 0d) {
						arr[0]  = nf.format(d1).toString();
						arr[1] = "Dr";
					} else {
						d1 *= -1 ;
						arr[0] = nf.format(d1).toString();
						arr[1] = "Cr";
					}
			}
			
		}
		//----------------------------
		LOGGER.debug("End of CalculateTrialBalance method");
		return arr;
	}

}
