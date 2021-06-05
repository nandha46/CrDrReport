package in.trident.crdr.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookView;
import in.trident.crdr.entities.FormData;
import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.DaybookServiceImpl;

/**
 * 
 * @author Nandha
 * 
 * @since 1.0
 */

@Controller
public class AppController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private CloseBalRepo closeBalRepo;

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute("pageTitle", "CrDr Home");
		LOGGER.trace("Inside Homepage controller");
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
		System.out.println("Password is : " + user.getPassword());
		user.setPassword(encodedPass);
		Set<Role> roles = new HashSet<Role>();
		Role role = new Role();
		// TODO Add another field in User object named role or pass it using JS
		role.setRoleName("");
		roles.add(role);
		user.setRoles(roles);
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
		LOGGER.info("Inside daybooks method");
		
		List<DaybookView> daybookViewObj = new DaybookServiceImpl(daybookRepo,closeBalRepo,accHeadRepo).daybookViewRange(formdata.getStartDate(), formdata.getEndDate());
		
		model.addAttribute("daybookViewObj",daybookViewObj);
		model.addAttribute("pageTitle", "Daybook View");
		
		return "daybooks";
	}

	// TODO Datatype of ledger list is changed to Hashmap so corresponding changes
	// are required in ledger.html

	@PostMapping("/ledger")
	public String listLedger(Model model, FormData formdata) {
		ArrayList<Daybook> daybooklist = new ArrayList<>();
		ArrayList<AccHead> headlist = new ArrayList<>();
		HashMap<String, ArrayList<Daybook>> listmap = new HashMap<String, ArrayList<Daybook>>();
		if (!formdata.isReportOrder()) { // false -> All acc heads
			headlist = accHeadRepo.findAllAccHead();
			for (AccHead acchead : headlist) {
				if (acchead.getAccCode() == 0) {
					// Intentionally left empty
				} else {
					daybooklist = daybookRepo.findDaybookByAccCodeAndDate(acchead.getAccCode(), formdata.getStartDate(),
							formdata.getEndDate());
					if (daybooklist.size() != 0)
						listmap.put(acchead.getAccName(), daybooklist);
				}
			}

		} else {

		}
		model.addAttribute("headlist", headlist);
		model.addAttribute("listmap", listmap);
		model.addAttribute("pageTitle", "CrDr Ledger");
		return "ledger";
	}

	@PostMapping("/trialBal")
	public String trialbal(Model model, FormData formdata) {
		/*
		 * Report order - Group/All Trial Balance As on - date Accounts with zero bal -
		 * yes/no level - 1,2,3,4,5,6
		 */

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
