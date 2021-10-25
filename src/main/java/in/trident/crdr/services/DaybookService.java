package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

import in.trident.crdr.models.DaybookView;

@Service
public interface DaybookService {
	
	 List<DaybookView> daybookViewRange(String startDate, String endDate, Long userid);
	 DaybookView createDaybook(String date, int day, Long userid);
	 
}
