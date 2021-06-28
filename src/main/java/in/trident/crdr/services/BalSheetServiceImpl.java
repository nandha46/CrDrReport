package in.trident.crdr.services;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.BalSheetForm;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.repositories.AccHeadRepo;
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
	private AccHeadRepo accHeadRepo;
	
	@Override
	public List<BalanceSheetView> createBalSheet(BalSheetForm balSheetForm) {
		List<BalanceSheetView> listBalSheet = new LinkedList<BalanceSheetView>();
		List<AccHead> list = accHeadRepo.findAllAccHead();
		Double value = new Double(100);
		list.forEach((name)->{
			BalanceSheetView bsv = new BalanceSheetView();
			bsv.setParticulars(name.getAccName());
			bsv.setCredit(value.toString());
			bsv.setDebit(value.toString());
			listBalSheet.add(bsv);
		});
		return listBalSheet;
	}

}
