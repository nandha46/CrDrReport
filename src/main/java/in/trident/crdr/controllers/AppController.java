package in.trident.crdr.controllers;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.Schedule;
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
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.BalanceSheetService;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.LedgerService;
import in.trident.crdr.services.PdfService;
import in.trident.crdr.services.TradingPLService;
import in.trident.crdr.services.TrialBalService;
import net.sf.jasperreports.engine.JRException;

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
	private DaybookService daybookService;

	@Autowired
	private TrialBalService trialBalService;

	@Autowired
	private TradingPLService tradingPLService;

	@Autowired
	private BalanceSheetService balanceSheetService;

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private CloseBalRepo closeBalRepo;
	
	@Autowired
	private PdfService pdfService;

	LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	// TODO need to implement user specific table data and filtering system

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute("pageTitle", "CrDr Home");
		LOGGER.warn("Use 'update users_roles set role_id = 1 where user_id = X' on MySQL DB to enable developer mode");
		return "index";
	}
	
	@GetMapping("/profile")
	public String showProfile(Model model) {
		model.addAttribute("pageTitle","Company Profile");
		LOGGER.info("Loading Profile...");
		return "profile";
	}
	
	@GetMapping("/reports")
	public String showReports(Model model) {
		model.addAttribute("pageTitle", "Reports");
		LOGGER.info("Reports Page loading...");
		return "reports";
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
	public String listDaybook(Model model, DaybookForm formdata, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.info("Inside daybooks method");
		List<DaybookView> daybookViewObj = daybookService.daybookViewRange(formdata.getStartDate(),
				formdata.getEndDate(), user.getId());
		model.addAttribute("daybookViewObj", daybookViewObj);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(formdata.getStartDate()));
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.info("Calendar Parsing Exception: Daybook Start date");
		}
		cal.add(Calendar.DAY_OF_MONTH, -1);
		model.addAttribute("openingBal",
				nf.format(closeBalRepo.findCloseBalByDate(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()))))
				.toString();
		model.addAttribute("pageTitle", "Daybook View");
		return "daybooks";
	}

	@GetMapping("/findLedger")
	public String findLedger(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		Collections.sort(accHeadList);
		LOGGER.info("Loading Ledgers...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "CrDr Ledger");
		model.addAttribute("formdata", new LedgerForm());
		return "findLedger";
	}

	@PostMapping("/ledger")
	public String listLedger(Model model, LedgerForm ledgerForm, @AuthenticationPrincipal CustomUserDetails user) throws FileNotFoundException, JRException {
		LOGGER.info("Ledger is Ready");
		List<LedgerView> listLedger = ledgerService.createLedgerViewList(ledgerForm, user.getId());
		pdfService.exportPdf(ledgerForm, user.getId());
		model.addAttribute("listLedger", listLedger);
		model.addAttribute("pageTitle", "CrDr Ledger");
		return "ledger";
	}

	@GetMapping("/findTrialBal")
	public String findTrial(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		Collections.sort(accHeadList);
		LOGGER.info("Loading Trial Balance...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Find Trial Balance");
		model.addAttribute("trialform", new TrialForm());
		return "findTrialBal";
	}

	@PostMapping("/trial")
	public String trialbal(Model model, TrialForm trialform, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.warn("Trial Balance Page Start");
		List<TrialView> listTrailView = trialBalService.createTrialBal(trialform, user.getId());
		LOGGER.info("Trial Balnce is Ready");
		model.addAttribute("listTrailView", listTrailView);
		model.addAttribute("pageTitle", "Trial Balance");
		return "trialBal";
	}

	@GetMapping("/findTradingPL")
	public String findTradingPl(Model model) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		Collections.sort(accHeadList);
		LOGGER.info("Loading TradingPL...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("pageTitle", "Trading - Profit and Loss");
		model.addAttribute("tradingPLForm", new TradingPLForm());
		return "findTradingPL";
	}

	@PostMapping("/tradingPL")
	public String tradingPL(Model model, TradingPLForm tradingPLForm, @AuthenticationPrincipal CustomUserDetails user) {
		List<TradingPLView> listTradingPL = tradingPLService.createTradingPL(tradingPLForm, user.getId());
		LOGGER.info("TradingPL is ready");
		model.addAttribute("listTradingPL", listTradingPL);
		model.addAttribute("pageTitle", "Trading - Profit & Loss");
		return "tradingPL";
	}

	@GetMapping("/findBalSheet")
	public String findBalSheet(Model model) {
		List<Schedule> accsList = scheduleRepo.findAll();
		Collections.sort(accsList);
		LOGGER.info("Loading Balance Sheet...");
		model.addAttribute("accsList", accsList);
		model.addAttribute("pageTitle", "Balance Sheet");
		model.addAttribute("balSheetForm", new BalSheetForm());
		return "findBalanceSheet";
	}

	@PostMapping("/BalanceSheet")
	public String balanceSheet(Model model, BalSheetForm balSheetForm, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.info("Balance Sheet Ready");
		List<BalanceSheetView> listBalSheet = balanceSheetService.createBalSheet(balSheetForm, user.getId());
		model.addAttribute("listBalSheet", listBalSheet);
		return "BalanceSheet";
	}

}
