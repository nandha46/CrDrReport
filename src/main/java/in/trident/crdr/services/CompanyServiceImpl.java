package in.trident.crdr.services;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.CompanyRepo;

@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyRepo companyRepo;
	
	@Autowired
	private CompSelectionRepo csr;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyServiceImpl.class);
	
	public void storeSelection(Long uid ,Long cid){
		CompanySelection cs0 = csr.findCompanyByUser(uid);
		if (cs0 == null) {
			Company c = companyRepo.findCompanyById(cid);
			  cs0 = new CompanySelection(c.getCompanyid(), c.getUserid(), c.getCompName(), "20"+c.getCompYear(), c.getFromDate(), c.getToDate(), c.getAddress1()+c.getAddress2()+c.getCity(), c.getcNoofAc(), c.getcNoofEntries(), Calendar.getInstance().getTime(), c.getCloseStk(), c.getOpenCash(), c.getCompType());
			 LOGGER.info("Selecting new company: --->"+cs0.toString());
			 csr.save(cs0); 
		} else {
		Long sid = cs0.getSid();
		Company c = companyRepo.findCompanyById(cid);
		 cs0 = new CompanySelection(sid, c.getCompanyid(), c.getUserid(), c.getCompName(), "20"+c.getCompYear(), c.getFromDate(), c.getToDate(), c.getAddress1()+c.getAddress2()+c.getCity(), c.getcNoofAc(), c.getcNoofEntries(), Calendar.getInstance().getTime(), c.getCloseStk(), c.getOpenCash(), c.getCompType());
		 LOGGER.info("Overwriting existing company: --->"+cs0.toString());
		 csr.save(cs0);
		}
	}

	@Override
	public List<String> listCompanies(Long uid) {
		return companyRepo.findUniqueCompanyByUser(uid);
	}

	@Override
	public Map<Long, String> listYears(String cname, Long uid) {
		Map<Long, String> map = new TreeMap<>();
		List<Company> companies = companyRepo.findCompanyYearsByName(cname,uid);
		companies.forEach(c->{
			map.put(c.getCompanyid(), "20"+c.getCompYear());
		});
		return map;
	}
	
	
	
}
