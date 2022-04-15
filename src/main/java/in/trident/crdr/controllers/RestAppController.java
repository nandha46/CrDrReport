package in.trident.crdr.controllers;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.trident.crdr.entities.Company;
import in.trident.crdr.repositories.CompanyRepo;
import in.trident.crdr.services.CompanyService;
import in.trident.crdr.services.CustomUserDetails;

@RestController
@RequestMapping("/api/v1/")
public class RestAppController {
	
	private CompanyService companyService;
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAppController.class);
	
	public RestAppController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@Autowired
	private CompanyRepo companyRepo;
	
	@GetMapping("/companies")
	public List<Company> list(){
		return companyRepo.findAll();
	}
	
	@GetMapping("/test")
	public String test(HttpServletRequest request) {
		Cookie[] cookies =  request.getCookies();
		return Arrays.stream(cookies).map(c -> c.getName() + " = "+ c.getValue()).collect(Collectors.joining(", "));
	}
	
	@GetMapping("/test2")
	public String test2(@CookieValue(value = "companyName",defaultValue = "defaultName") Cookie cookie ,HttpServletRequest request) {
		
		return cookie.getName()+ " Expires after/on: " + cookie.getMaxAge();
	}
	
	@GetMapping("/years")
	public ResponseEntity<?> getCompanyYears(@AuthenticationPrincipal CustomUserDetails user, @RequestParam(name = "companyName") String companyName) {
		Map<Long, String> years = companyService.listYears(companyName, user.getId());
		return ResponseEntity.ok(years);
	}
}
