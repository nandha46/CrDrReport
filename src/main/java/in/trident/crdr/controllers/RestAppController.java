package in.trident.crdr.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.trident.crdr.entities.Company;
import in.trident.crdr.repositories.CompanyRepo;

@RestController
@RequestMapping("/api/v1/")
public class RestAppController {

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
}
