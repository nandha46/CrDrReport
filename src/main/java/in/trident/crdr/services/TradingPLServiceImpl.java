package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.TradingPLForm;
import in.trident.crdr.models.TradingPLView;
/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 21 Jun 2021
 * 
 * @version 0.0.5b
 *
 */
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;
/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.5b
 * @since 21 Jun 2021
 *
 */
@Service
public class TradingPLServiceImpl implements TradingPLService {

	@Autowired
	private AccHeadRepo accHeadRepo;
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TradingPLServiceImpl.class);
	
	private static int counter = 0;
	
	@Override
	public List<TradingPLView> createTradingPL(TradingPLForm tradingPLForm) {
		Profiler profiler = new Profiler("TradingPLService");
		profiler.setLogger(LOGGER);
		profiler.start("Start TradingPL Service");
		LinkedHashSet<TradingPLView> tradingPLViewSet = new LinkedHashSet<>();
		if(tradingPLForm.isReportOrder()) {
			List<AccHead> tradingAccs = accHeadRepo.findTradingPLAccs();
			Collections.sort(tradingAccs);
			tradingAccs.forEach((accs)->{
				if (accs.getOrderCode() == 3 || accs.getOrderCode() == 4) {
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					String[] arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate());
					if(arr[1].equals("Cr")) {
						tplv.setDebit("");
						tplv.setCredit(arr[0]);
					} else {
						tplv.setDebit(arr[0]);
						tplv.setCredit("");
					}
					if (tplv.getDebit().equals("ZeroB") && tradingPLForm.isZeroBal()) {
						tplv.setDebit(tplv.getDebit().replace("ZeroB", "0"));
						tradingPLViewSet.add(tplv);
					} else {
						// If Debit returns ZeroB and isZeroBal is true tradingPLview won't get added to view
						if (tplv.getDebit().equals("ZeroB")) {
							
						} else {
							tradingPLViewSet.add(tplv);
						 }
					}
					tplv.setLevel(accs.getLevel1());
				} else {
					String[] arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate());
					if (counter < 1) {
					TradingPLView grossProfit = new TradingPLView();
					grossProfit.setParticulars("Gross Profit");
					if(arr[1].equals("Cr")) {
						grossProfit.setDebit("");
						grossProfit.setCredit(arr[0]);
					} else {
						grossProfit.setDebit(arr[0]);
						grossProfit.setCredit("");
					}
					grossProfit.setLevel(0);
					tradingPLViewSet.add(grossProfit);
					TradingPLView total = new TradingPLView();
					total.setParticulars("Total");
					arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate());
					if(arr[1].equals("Cr")) {
						total.setDebit("");
						total.setCredit(arr[0]);
					} else {
						total.setDebit(arr[0]);
						total.setCredit("");
					}
					
					total.setLevel(0);
					tradingPLViewSet.add(total);
					TradingPLView grossProfitB = new TradingPLView();
					grossProfitB.setParticulars("Gross Profit Before");
					arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate());
					if(arr[1].equals("Cr")) {
						grossProfitB.setDebit("");
						grossProfitB.setCredit(arr[0]);
					} else {
						grossProfitB.setDebit(arr[0]);
						grossProfitB.setCredit("");
					}
					grossProfitB.setLevel(0);
					tradingPLViewSet.add(grossProfitB); 
					counter++;
				}
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate());
					if(arr[1].equals("Cr")) {
						tplv.setDebit("");
						tplv.setCredit(arr[0]);
					} else {
						tplv.setDebit(arr[0]);
						tplv.setCredit("");
					}
					if (tplv.getDebit().equals("ZeroB") && tradingPLForm.isZeroBal()) {
						tplv.setDebit(tplv.getDebit().replace("ZeroB", "0"));
						tradingPLViewSet.add(tplv);
					} else {
						// If Debit returns ZeroB and isZeroBal is true tradingPLview won't get added to view
						if (tplv.getDebit().equals("ZeroB")) {
							
						} else {
							tradingPLViewSet.add(tplv);
						 }
					}
					tplv.setLevel(accs.getLevel1());
					
				}
			});
		} else { // Report order group
			
		}
		
		TradingPLView netProfit = new TradingPLView();
		netProfit.setParticulars("Net Profit");
		String[] arr = {"31107.50","Dr"};
		if(arr[1].equals("Cr")) {
			netProfit.setDebit("");
			netProfit.setCredit(arr[0]);
		} else {
			netProfit.setDebit(arr[0]);
			netProfit.setCredit("");
		}
		netProfit.setLevel(0);
		tradingPLViewSet.add(netProfit);
		TradingPLView total2 = new TradingPLView();
		total2.setParticulars("Total");
		arr[0] = "58663.00";
		arr[1] = "Cr";
		if(arr[1].equals("Cr")) {
			total2.setDebit("");
			total2.setCredit(arr[0]);
		} else {
			total2.setDebit(arr[0]);
			total2.setCredit("");
		}
		total2.setLevel(0);
		tradingPLViewSet.add(total2);
		List<TradingPLView> listTradingPLView = new LinkedList<TradingPLView>(tradingPLViewSet);
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n"+ti.toString());
		ti.log();
		return listTradingPLView;
	}

	@Override
	public String[] calculateTradingBalance(Integer code, String endDate) {
		LOGGER.debug("Start of CalculateTradingBalance method");
		String[] arr = {"",""}; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = {"","Cr"};
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code); 
		Double d2 = accHeadRepo.findDrAmt(code);
		
		if(d1 == 0d) { // If Dr is the Budget Amt
			LOGGER.debug("Budget is Dr");
			LOGGER.debug("Acc Code :"+code.toString());
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
			LOGGER.debug("Budget is Cr");
			LOGGER.debug("Acc Code :"+code.toString());
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
		LOGGER.debug("End of CalculateTradingBalance method");
		return arr;
		
	}

}
