package in.trident.crdr.services;

import java.util.LinkedList;
import java.util.List;

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
	
	@Override
	public List<TradingPLView> createTradingPL(TradingPLForm tradingPLForm) {
		List<TradingPLView> listTradingPLView = new LinkedList<TradingPLView>();
		if(tradingPLForm.isReportOrder()) {
			List<AccHead> tradingInc = accHeadRepo.findAccHeadByAccType("Trading Income");
			List<AccHead> tradingExp = accHeadRepo.findAccHeadByAccType("Trading Expense");
			//TODO select between order code 3 and 6
			TradingPLView grossProfit = new TradingPLView();
			grossProfit.setParticulars("Gross Profit");
			grossProfit.setCredit(null);
			grossProfit.setDebit(null);
			TradingPLView total = new TradingPLView();
			total.setParticulars("Total");
			total.setDebit(null);
			total.setCredit(null);
			List<AccHead> pnLInc = accHeadRepo.findAccHeadByAccType("P & L Income");
			List<AccHead> pnLExp = accHeadRepo.findAccHeadByAccType("P & L Expense");
		} else {
			
		}
		return listTradingPLView;
	}

}
