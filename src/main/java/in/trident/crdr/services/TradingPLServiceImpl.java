package in.trident.crdr.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		List<String> list =  accHeadRepo.findAccNames();
		list.forEach((acchead)-> {
			TradingPLView tradingPLView = new TradingPLView();
			tradingPLView.setParticulars(acchead);
			tradingPLView.setDebit("10025");
			tradingPLView.setCredit("12458");
			listTradingPLView.add(tradingPLView);
		});
		
		return listTradingPLView;
	}

}
