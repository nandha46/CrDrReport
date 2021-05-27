package in.trident.crdr.services;

import java.util.List;

import in.trident.crdr.entities.DaybookView;

public interface DaybookService {
	
	 DaybookView createDaybook(String date);
	 List<DaybookView> daybookViewRange(String startDate, String endDate);
	 
}
