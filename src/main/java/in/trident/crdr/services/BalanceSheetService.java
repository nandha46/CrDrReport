package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.TplBalView;
import in.trident.crdr.models.BalanceSheetView;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 26 Jun 2021
 * 
 * @version 0.0.5b
 *
 */
@Service
public interface BalanceSheetService {
	
	List<BalanceSheetView> createBalSheet(CommonForm balSheetForm, Long uid, Long cid); 
	String[] calculateLedgerBalance(Integer code, String endDate, Long uid, Long cid);
	List<List<TplBalView>> createBalSheet2(CommonForm balSheetForm, Long uid, Long cid);
	List<BalanceSheetView> createReportGroup(List<BalanceSheetView> list, int level);
}
