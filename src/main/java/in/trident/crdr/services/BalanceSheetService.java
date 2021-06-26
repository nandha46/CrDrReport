package in.trident.crdr.services;

import java.util.List;

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
public interface BalanceSheetService {
	
	List<BalanceSheetView> createBalSheet(BalSheetForm balSheetForm); 
}
