package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.repositories.AccHeadRepo;

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
public class TrialServiceImpl implements TrialBalService {

	@Autowired
	private AccHeadRepo accHeadRepo;
	
	@Override
	public List<TrialView> createTrialBal(TrialForm trialform) {
		List<TrialView> listTrialview = new LinkedList<TrialView>();
		List<AccHead> list = accHeadRepo.findAllAccHead();
		Collections.sort(list);
		if (trialform.isReportOrder()) {
			Double value = new Double(100);
			list.forEach((acc)->{
				TrialView tv = new TrialView();
				tv.setAccName(acc.getAccName());
				tv.setDebit(value.toString());
				tv.setCredit(value.toString());
				listTrialview.add(tv);
			});
		} else {
			
		}
		
		return listTrialview;
	}

}
