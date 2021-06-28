package in.trident.crdr.services;

import java.util.List;

import org.springframework.stereotype.Service;

import in.trident.crdr.models.DaybookView;

@Service
public interface DaybookService {
	
	 DaybookView createDaybook(String date);
	 List<DaybookView> daybookViewRange(String startDate, String endDate);
	 
}
