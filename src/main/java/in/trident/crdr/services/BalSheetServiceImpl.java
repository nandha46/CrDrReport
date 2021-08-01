package in.trident.crdr.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.BalSheetForm;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.ScheduleRepo;
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
public class BalSheetServiceImpl implements BalanceSheetService {

	@Autowired
	private ScheduleRepo scheduleRepo;
	
	@Override
	public List<BalanceSheetView> createBalSheet(BalSheetForm balSheetForm) {
		List<BalanceSheetView> listBalSheet = new LinkedList<BalanceSheetView>();
		if (balSheetForm.isReportOrder()) {
			
		} else {
			List<Schedule> list = scheduleRepo.findAll();
			
		}
		return listBalSheet;
	}

}
