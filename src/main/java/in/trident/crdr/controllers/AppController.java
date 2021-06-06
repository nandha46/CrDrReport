package in.trident.crdr.controllers;

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
import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.models.DaybookForm;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.DaybookServiceImpl;
import in.trident.crdr.services.LedgerServiceImpl;

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
	public String listDaybook(Model model, DaybookForm formdata) {
		LOGGER.info("Inside daybooks method");
		
		List<DaybookView> daybookViewObj = new DaybookServiceImpl(daybookRepo,closeBalRepo,accHeadRepo).daybookViewRange(formdata.getStartDate(), formdata.getEndDate());
		
		model.addAttribute("daybookViewObj",daybookViewObj);
		model.addAttribute("pageTitle", "Daybook View");
		
		return "daybooks";
	}

	// TODO Datatype of ledger list is changed to Hashmap so corresponding changes
	// are required in ledger.html

	@PostMapping("/ledger")
	public String listLedger(Model model, LedgerForm ledgerForm) {
		List<LedgerView> listLedger = new LedgerServiceImpl().createLedgerViewList(ledgerForm);
		model.addAttribute("listLedger",listLedger);
		model.addAttribute("pageTitle", "CrDr Ledger");
		return "ledger";
	}

	@PostMapping("/trialBal")
	public String trialbal(Model model, DaybookForm formdata) {
		/*
		 * Report order - Group/All Trial Balance As on - date Accounts with zero bal -
		 * yes/no level - 1,2,3,4,5,6
		 */

		return "trialbal";
	}

	@GetMapping("/findDaybook")
	public String findDaybook(Model model) {
		model.addAttribute("formdata", new DaybookForm());
		model.addAttribute("pageTitle", "CrDr Daybook");
		return "findDaybook";
	}

	@GetMapping("/findLedger")
	public String findLedger(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "CrDr Ledger");
		model.addAttribute("formdata", new LedgerForm());
		return "findLedger";
	}
	
	@GetMapping("/findTrialBal")
	public String findTrial(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Trial Balance");
		model.addAttribute("formdata", new DaybookForm());
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
