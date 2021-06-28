package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

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
@Service
public interface TradingPLService {

	List<TradingPLView> createTradingPL(TradingPLForm tradingPLForm);

}
