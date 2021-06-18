package in.trident.crdr.services;

import java.util.List;

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
public interface TrialBalService {

	 List<TrialView> createTrialBal(TrialForm trialform);
	
}
