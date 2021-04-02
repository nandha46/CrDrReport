package in.trident.crdr.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookBalance;
import in.trident.crdr.entities.FormData;
import in.trident.crdr.entities.LedgerBalance;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.UserRepository;

@Controller
public class AppController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private AccHeadRepo accHeadRepo;

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute("pageTitle", "CrDr Home");
		return "index";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "signup_form";
	}

	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
		String encodedPass = passEncoder.encode(user.getPassword());
		System.out.println("Password is : "+user.getPassword());
		user.setPassword(encodedPass);
		userRepo.save(user);
		// TODO Check if user already exists by email id
		return "register_success";
	}

	@GetMapping("/users")
	public String listUsers(Model model) {
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		return "users";
	}

	@PostMapping("/daybooks")
	public String listDaybook(Model model, FormData formdata) {
		//TODO move this impl to service method for clean code
		int days = formdata.findDays(formdata.getStartDate(), formdata.getEndDate());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		try {
			calender.setTime(df.parse(formdata.getStartDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ArrayList<ArrayList<Daybook>> listOflist = new ArrayList<ArrayList<Daybook>>();
		ArrayList<Daybook> daybookList;
		for (int i = 0; i <= days; i++) {
			daybookList = daybookRepo.findDaybookByDate(df.format(calender.getTime()));
			listOflist.add(daybookList);
			calender.add(Calendar.DATE, 1);
		}
		model.addAttribute("listOflist", listOflist);
		model.addAttribute("pageTitle", "Daybook View");
		model.addAttribute("DaybookBalance", new DaybookBalance());
		return "daybooks";
	}

	@PostMapping("/ledger")
	public String listLedger(Model model, FormData formdata) {
		ArrayList<Daybook> daybooklist = new ArrayList<>();
		ArrayList<AccHead> headlist = new ArrayList<>();
		HashMap<String,ArrayList<Daybook>> listmap = new HashMap<String,ArrayList<Daybook>>();
		if(!formdata.isReportOrder()) { // false -> All acc heads
			 headlist = accHeadRepo.findAllAccHead();
			for (AccHead acchead : headlist) {
				if (acchead.getAccCode() == 0) {
					// Intentionally left empty
				}
				else {
					daybooklist = daybookRepo.findDaybookByAccCodeAndDate(acchead.getAccCode(), formdata.getStartDate(), formdata.getEndDate());
					if (daybooklist.size() != 0)
					listmap.put(acchead.getAccName(), daybooklist);
				}
			}
			
		}
		else {
			
		}
		model.addAttribute("headlist", headlist);
		model.addAttribute("listmap", listmap);
		model.addAttribute("LedgerBalance", new LedgerBalance());
		model.addAttribute("pageTitle", "CrDr Ledger");
		return "ledger";
	}

	@PostMapping("/trialBal")
	public String trialbal(Model model, FormData formdata) {
		/* Report order - Group/All
		 * Trial Balance As on - date
		 * Accounts with zero bal - yes/no
		 * level - 1,2,3,4,5,6
		 * */
		
		
		return "trialbal";
	}
	
	@GetMapping("/findDaybook")
	public String findDaybook(Model model) {
		model.addAttribute("formdata", new FormData());
		model.addAttribute("pageTitle", "CrDr Daybook");
		return "findDaybook";
	}

	@GetMapping("/findLedger")
	public String findLedger(Model model) {
		ArrayList<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "CrDr Ledger");
		model.addAttribute("formdata", new FormData());
		return "findLedger";
	}

	@GetMapping("/findTrialBal")
	public String findTrial(Model model) {
		ArrayList<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Trial Balance");
		model.addAttribute("formdata", new FormData());
		return "findTrialBal";
	}
	
	@GetMapping("/findTradingPL")
	public String findTradingPl(Model model) {
		model.addAttribute("pageTitle", "Trading - Profit and Loss");
		return "production";
	}
	
	@GetMapping("/findBalSheet")
	public String findBalSheet(Model model) {
		model.addAttribute("pageTitle", "Balance Sheet");
		return "production";
	}
	
}
