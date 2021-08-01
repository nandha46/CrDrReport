package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

import in.trident.crdr.models.BalSheetForm;
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
	
	List<BalanceSheetView> createBalSheet(BalSheetForm balSheetForm); 
	String[] calculateLedgerBalance(Integer code, String endDate);
}
