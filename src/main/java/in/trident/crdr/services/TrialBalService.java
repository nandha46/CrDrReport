package in.trident.crdr.services;

import java.util.List;
import org.springframework.stereotype.Service;

import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 18 Jun 2021
 * 
 * @version 0.0.5b
 *
 */
@Service
public interface TrialBalService {

	 List<TrialView> createTrialBal(TrialForm trialform, Long uid, Long cid);
	 String[] calculateTrialBalance(Integer code, String endDate, Long uid, Long cid);
	 List<TrialView> createReportGroup(List<TrialView> list, int level);
	
}
