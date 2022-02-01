package in.trident.crdr.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface CompanyService {

	public void storeSelection(Long uid,Long cid);
	public List<String> listCompanies(Long uid);
	public Map<Long,String> listYears(String cname, Long uid);
}
