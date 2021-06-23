package in.trident.crdr.services;

import java.util.List;

import in.trident.crdr.models.TradingPLForm;
import in.trident.crdr.models.TradingPLView;

/** 
* @author Nandhakumar Subramanian
* 
* @since 21 Jun 2021
* 
* @version 0.0.5b
*
*/
public interface TradingPLService {

	List<TradingPLView> createTradingPL(TradingPLForm tradingPLForm);
	

}
