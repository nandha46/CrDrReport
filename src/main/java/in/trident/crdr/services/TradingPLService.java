package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.TplBalView;
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

	List<TradingPLView> createTradingPL(CommonForm tradingPLForm, Long uid, Long cid);
	String[] calculateTradingBalance(Integer code, String endDate, Long uid, Long cid);
	List<List<TplBalView>> createTradingPL2(CommonForm tradingPLForm, Long uid, Long cid);

}
