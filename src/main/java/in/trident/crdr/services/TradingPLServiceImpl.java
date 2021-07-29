package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	@Override
	public List<TradingPLView> createTradingPL(TradingPLForm tradingPLForm) {
		
		LinkedHashSet<TradingPLView> tradingPLViewSet = new LinkedHashSet<>();
		if(tradingPLForm.isReportOrder()) {
			List<AccHead> tradingAccs = accHeadRepo.findTradingPLAccs();
			Collections.sort(tradingAccs);
			tradingAccs.forEach((accs)->{
				if (accs.getOrderCode() == 3 || accs.getOrderCode() == 4) {
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					tplv.setCredit(null);
					tplv.setDebit(null);
					tradingPLViewSet.add(tplv);
				} else {
					TradingPLView grossProfit = new TradingPLView();
					grossProfit.setParticulars("Gross Profit");
					grossProfit.setCredit(null);
					grossProfit.setDebit(null);
					tradingPLViewSet.add(grossProfit);
					TradingPLView total = new TradingPLView();
					total.setParticulars("Total");
					total.setDebit(null);
					total.setCredit(null);
					tradingPLViewSet.add(total);
					TradingPLView grossProfitB = new TradingPLView();
					total.setParticulars("Gross Profit Before");
					total.setDebit(null);
					total.setCredit(null);
					tradingPLViewSet.add(grossProfitB);
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					tplv.setCredit(null);
					tplv.setDebit(null);
					tradingPLViewSet.add(tplv);
					TradingPLView netProfit = new TradingPLView();
					grossProfit.setParticulars("Net Profit");
					grossProfit.setCredit(null);
					grossProfit.setDebit(null);
					tradingPLViewSet.add(netProfit);
					TradingPLView total2 = new TradingPLView();
					grossProfit.setParticulars("Total");
					grossProfit.setCredit(null);
					grossProfit.setDebit(null);
					tradingPLViewSet.add(total2);
				}
			});
		} else { // Report order group
			
		}
		List<TradingPLView> listTradingPLView = new LinkedList<TradingPLView>(tradingPLViewSet);
		return listTradingPLView;
	}

	@Override
	public String[] calculateTradingBalance(Integer code, String endDate) {
		LOGGER.warn("Start of CalculateTradingBalance method");
		String[] arr = {"",""}; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = {"","Cr"};
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code); 
		Double d2 = accHeadRepo.findDrAmt(code);
		
		if(d1 == 0d) { // If Dr is the Budget Amt
			LOGGER.info("Budget is Dr");
			LOGGER.info("Acc Code :"+code.toString());
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
			LOGGER.info("Budget is Cr");
			LOGGER.info("Acc Code :"+code.toString());
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
		LOGGER.warn("End of CalculateTradingBalance method");
		return arr;
		
	}

}
