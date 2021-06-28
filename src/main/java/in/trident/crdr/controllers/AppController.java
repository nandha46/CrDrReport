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
import in.trident.crdr.models.TradingPLForm;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.models.BalSheetForm;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.DaybookForm;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.BalanceSheetService;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.LedgerService;
import in.trident.crdr.services.TradingPLService;
import in.trident.crdr.services.TrialBalService;

/**
 * 
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since day 1
 * @version 0.0.1
 */

@Controller
public class AppController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private LedgerService ledgerService;

	@Autowired
	private DaybookService daybookSerice;

	@Autowired
	private TrialBalService trialBalService;
	
	@Autowired
	private TradingPLService tradingPLService;

	@Autowired
	private BalanceSheetService balanceSheetService;

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute("pageTitle", "CrDr Home");
		LOGGER.warn("Use 'update users_roles set role_id = 1 where user_id = X' on MySQL DB to enable developer mode");
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

	@GetMapping("/findDaybook")
	public String findDaybook(Model model) {
		model.addAttribute("formdata", new DaybookForm());
		model.addAttribute("pageTitle", "CrDr Daybook");
		return "findDaybook";
	}

	@PostMapping("/daybooks")
	public String listDaybook(Model model, DaybookForm formdata) {
		LOGGER.info("Inside daybooks method");
		List<DaybookView> daybookViewObj = daybookSerice.daybookViewRange(formdata.getStartDate(),
				formdata.getEndDate());
		model.addAttribute("daybookViewObj", daybookViewObj);
		model.addAttribute("pageTitle", "Daybook View");
		return "daybooks";
	}

	@GetMapping("/findLedger")
	public String findLedger(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "CrDr Ledger");
		model.addAttribute("formdata", new LedgerForm());
		return "findLedger";
	}

	@PostMapping("/ledger")
	public String listLedger(Model model, LedgerForm ledgerForm) {
		LOGGER.warn("Ledger Page Start");
		List<LedgerView> listLedger = ledgerService.createLedgerViewList(ledgerForm);
		model.addAttribute("listLedger", listLedger);
		model.addAttribute("pageTitle", "CrDr Ledger");
		return "ledger";
	}

	@GetMapping("/findTrialBal")
	public String findTrial(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Find Trial Balance");
		model.addAttribute("trialform", new TrialForm());
		return "findTrialBal";
	}

	@PostMapping("/trial")
	public String trialbal(Model model, TrialForm trialform) {
		LOGGER.warn("Trial Balance Page Start");
		List<TrialView> listTrailView = trialBalService.createTrialBal(trialform);
		model.addAttribute("listTrailView", listTrailView);
		model.addAttribute("pageTitle", "Trial Balance");
		return "trialBal";
	}

	@GetMapping("/findTradingPL")
	public String findTradingPl(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Trading - Profit and Loss");
		model.addAttribute("tradingPLForm", new TradingPLForm());
		return "findTradingPL";
	}

	@PostMapping("/tradingPL")
	public String tradingPL(Model model, TradingPLForm tradingPLForm) {
		LOGGER.warn("TradingPL Page Start");
		List<TradingPLView> listTradingPL = tradingPLService.createTradingPL(tradingPLForm);
		model.addAttribute("listTradingPL", listTradingPL);
		model.addAttribute("pageTitle", "Trading - Profit & Loss");
		return "tradingPL";
	}

	@GetMapping("/findBalSheet")
	public String findBalSheet(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Balance Sheet");
		model.addAttribute("balSheetForm", new BalSheetForm());
		return "findBalanceSheet";
	}

	@PostMapping("/BalanceSheet")
	public String balanceSheet(Model model, BalSheetForm balSheetForm) {
		LOGGER.warn("Balance Sheet start");
		List<BalanceSheetView> listBalSheet = balanceSheetService.createBalSheet(balSheetForm);
		model.addAttribute("listBalSheet", listBalSheet);
		return "BalanceSheet";
	}

}
