package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

import in.trident.crdr.models.Dailybooks;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 0.0.5
 *
 */
@Service
public interface LedgerService {

	List<LedgerView> createLedgerViewList(LedgerForm ledgerForm);
	List<Dailybooks> createDailybooks(Integer code, String startDate, String endDate, Double bal);
	LedgerView createLedgerView(Integer code, LedgerForm ledgerForm);
	
}
