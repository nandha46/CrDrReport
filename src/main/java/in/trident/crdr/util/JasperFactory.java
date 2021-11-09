package in.trident.crdr.util;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.repositories.DaybookRepository;

public class JasperFactory {
	
	
	@Autowired
	private static DaybookRepository daybookRepo;
	
	public static Collection generateCollection() {
		Vector collection = new Vector<>();
//		List<LedgerView> ledgers = ledgerService.createLedgerViewList(ledgerForm, id);
		List<Daybook> daybooks = daybookRepo.findDaybookRange("2018-04-01", "2019-03-31");
		collection.addAll(daybooks);
		return collection;
	}

}
