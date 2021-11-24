package in.trident.crdr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.trident.crdr.entities.Company;
import in.trident.crdr.repositories.CompanyRepo;

@RestController
public class RestAppController {

	@Autowired
	private CompanyRepo companyRepo;
	
	@GetMapping("/companies")
	public List<Company> list(){
		return companyRepo.findAll();
	}
}
