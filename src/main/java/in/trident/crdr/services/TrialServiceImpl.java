package in.trident.crdr.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
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

	@Autowired
	private CloseBalRepo closeBalRepo;
	
	@Autowired
	private CompSelectionRepo csr;

	private static final Logger LOGGER = LoggerFactory.getLogger(TrialServiceImpl.class);

	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));
	
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private static int counter = 0;

	@Override
	public List<TrialView> createTrialBal(TrialForm trialform, Long uid, Long cid) {
		Profiler profiler = new Profiler("TrialBalService");
		profiler.setLogger(LOGGER);
		profiler.start("CreateTrialBal");
		LOGGER.info("Start TrialBal Service");
		counter = 0;
		List<TrialView> listTrialview = new LinkedList<TrialView>();
		List<AccHead> list = accHeadRepo.findAllAccHead(uid, cid);
		Collections.sort(list);
			
			list.forEach((acc) -> {
				if (counter == 1) {
					counter++;
					TrialView tv1 = new TrialView();
					tv1.setAccName("Cash on Hand");
					tv1.setLevel(2);
					tv1.setHeader(true);
					Double d = closeBalRepo.findCloseBalByDate(trialform.getEndDate(), uid, cid);
					if (d < 0) {
						tv1.setCredit(nf.format(Math.abs(d)).toString());
						tv1.setDebit("");
					} else {
						tv1.setCredit("");
						tv1.setDebit(nf.format(Math.abs(d)).toString());
					}
					listTrialview.add(tv1); 
				}
				
				if (counter == 0) {
					counter++;
				} 
				
				TrialView tv = new TrialView();
				tv.setAccName(acc.getAccName());
				String[] arr = calculateTrialBalance(acc.getAccCode(), trialform.getEndDate(), uid, cid);
				if (arr[1].equals("Cr")) {
					tv.setDebit("");
					tv.setCredit(arr[0]);
				} else {
					tv.setDebit(arr[0]);
					tv.setCredit("");
				}
				if (acc.getAccCode() == 0) {
					tv.setHeader(true);
				} else {
					tv.setHeader(false);
				}
				tv.setLevel(acc.getLevel1());
				
				if (trialform.isZeroBal() && ((tv.getDebit().equals("0.00") && tv.getCredit().isEmpty())
						|| (tv.getCredit().equals("0.00") && tv.getDebit().isEmpty()))) {
					// Intentionally left empty to remove ZeroBal accounts
				} else {
					listTrialview.add(tv);
				}
			});
		Double debitTotal = listTrialview.stream().filter(x -> !x.getDebit().isEmpty())
				.mapToDouble(x -> Double.parseDouble(x.getDebit().replace(",", ""))).sum();
		Double creditTotal = listTrialview.stream().filter(x -> !x.getCredit().isEmpty())
				.mapToDouble(x -> Double.parseDouble(x.getCredit().replace(",", ""))).sum();
		listTrialview
				.add(new TrialView("Total", nf.format(debitTotal).toString(), nf.format(creditTotal).toString(), 1,true));
		TimeInstrument ti = profiler.stop();
		ti.log();
	//	LOGGER.info(listTrialview.toString());
		if (trialform.isReportOrder()) {
			List<TrialView> altList = createReportGroup(listTrialview, trialform.getLevel());
			return altList;
		} else {
		return listTrialview;	
		}
	}

	@Override
	public String[] calculateTrialBalance(Integer code, String endDate, Long uid, Long cid) {
		LOGGER.debug("Start of CalculateTrialBalance method");
		CompanySelection cs =  csr.findCompanyByUser(uid);
		String startDate = cs.getFromDate().format(dateFormat);
		String[] arr = { "", "" }; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = { "", "Cr" };
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code, uid, cid);
		Double d2 = accHeadRepo.findDrAmt(code, uid, cid);
		if (d1 == 0d) {
			// Prev year Bal is Dr
			LOGGER.debug("AccCode" + code + "Opening Debit: " + d2);
			Double tmp = daybookRepo.openBal(code, startDate, endDate, uid, cid);
			// Null check daybook repos return value
			if (tmp == null) {
				// d2 is also zero, so there is no txn & no prev year bal
				// whether d2 is 0 or Somevalue Balance is Dr
				arr[0] = nf.format(Math.abs(d2)).toString();
				arr[1] = "Dr";
				return arr;
			} else if (tmp > 0d || tmp == 0d) {
				// tmp is +ve so Cr
				tmp = d2 - tmp;
				if (tmp > 0d) {
					arr[0] = nf.format(Math.abs(tmp)).toString();
					arr[1] = "Dr";
				} else {
					arr[0] = nf.format(Math.abs(tmp)).toString();
					arr[1] = "Cr";
				}
			} else {
				d2 = d2 - tmp;
				arr[0] = nf.format(Math.abs(d2)).toString();
				arr[1] = "Dr";
			}
		} else { // then Prev year Bal is Cr
			Double tmp = daybookRepo.openBal(code, startDate, endDate, uid, cid);
			if (tmp == null) {
				arr[0] = nf.format(Math.abs(d1)).toString();
				arr[1] = "Cr";
				return arr;
			} else if (tmp > 0d || tmp == 0d) {
				// tmp is +ve so Cr
				tmp = d1 + tmp;
				arr[0] = nf.format(Math.abs(tmp)).toString();
				arr[1] = "Cr";
			} else {
				// tmp is -ve so Dr
				d1 = d1 + tmp;
				if (d1 > 0d) {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Cr";
				} else {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Dr";
				}
			}

		}
		// ----------------------------
		LOGGER.debug("End of CalculateTrialBalance method");
		return arr;
	}

	@Override
	public List<TrialView> createReportGroup(List<TrialView> listTrialview, int level) {
		List<TrialView> altList = new ArrayList<>();
		for (int i=0; i < listTrialview.size()-1; i++) {
				Double debit = 0.0d;
				Double credit = 0.0d;
				String d, c ;
				if(listTrialview.get(i+1).getLevel() > level ) {
			//		LOGGER.info("Head name: "+listTrialview.get(i).getAccName());
				
					for (int j = i+1; j < listTrialview.size(); j++) {
				//		LOGGER.info("Consumed: "+ listTrialview.get(j));
						d = listTrialview.get(j).getDebit().replace(",", "");
						c = listTrialview.get(j).getCredit().replace(",", "");
						if (!d.isEmpty())
						debit += Double.parseDouble(d);
						
						if(!c.isEmpty())
						credit += Double.parseDouble(c); 
				//		LOGGER.info("Debit: "+debit+" Credit: "+credit);
						if (listTrialview.get(j+1).getLevel() < listTrialview.get(j).getLevel() ) {
							altList.add(new TrialView(listTrialview.get(i).getAccName(),nf.format(debit).toString(),nf.format(credit).toString(),listTrialview.get(i).getLevel(),listTrialview.get(i).isHeader()));
							i=j;
					//		LOGGER.info("Break out of loop");
							break;
						}	
					}
					
				} else {
					altList.add(listTrialview.get(i));
				}
		}
		altList.add(listTrialview.get(listTrialview.size()-1));	
		return altList;
	}

	
}
