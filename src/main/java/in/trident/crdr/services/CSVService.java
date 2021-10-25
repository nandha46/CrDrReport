package in.trident.crdr.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.util.CSVUtil;

@Service
public class CSVService {
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	public void save(Long userid ,MultipartFile file) {
		try {
			List<Daybook> daybooks = CSVUtil.csvToDaybook(userid, file.getInputStream());
			daybookRepo.saveAll(daybooks);
		} 
		catch (IOException e) {
			throw new RuntimeException("Failed to store CSV data: "+e.getMessage());
		}
	}
}
