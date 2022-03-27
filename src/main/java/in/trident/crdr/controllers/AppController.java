package in.trident.crdr.controllers;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.models.TplBalView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.DaybookForm;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.services.BalanceSheetService;
import in.trident.crdr.services.CompanyService;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.LedgerService;
import in.trident.crdr.services.PdfService;
import in.trident.crdr.services.TradingPLService;
import in.trident.crdr.services.TrialBalService;
import in.trident.crdr.util.CSVUtil;
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

	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String PAGE_TITLE = "pageTitle";

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
	private CompanyService companyService;

	@Autowired
	private CompSelectionRepo csr;

	@Autowired
	private PdfService pdfService;

	LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	@GetMapping("/access")
	public void runDB() {
		CSVUtil csv = new CSVUtil();
		csv.processAccessDatabse();
	}

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute(PAGE_TITLE, "CrDr Home");
		return "index";
	}

	@GetMapping("/login")
	public String showlogin(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.info("Inside login controller");
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			LOGGER.info("Anonymous user");
			return "login";
		}
		LOGGER.info("Redirection");
		return "redirect:/company_selection";
	}

	@GetMapping("/company_selection")
	public String showCompanies(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		model.addAttribute(PAGE_TITLE, "Select Company");
		model.addAttribute("companies", companyService.listCompanies(user.getId()));
		return "company_select";
	}

	@PostMapping("/companyselect")
	public String showYears(@RequestParam(value = "companyName") String companyName, Model model,
			@AuthenticationPrincipal CustomUserDetails user, HttpServletResponse response) {
		LOGGER.info("Company Name: {}", companyName);
		if (companyName == null) {
			return "redirect:/company_selection";
		}
		// Setting cookie
		Cookie cookie = new Cookie("companyName", companyName.replace(" ", "_"));
		cookie.setPath("/");
		cookie.setMaxAge(2 * 60 * 60);
		cookie.setHttpOnly(true);
		cookie.setComment("Currently selected Company name of the user");
		response.addCookie(cookie);

		Map<Long, String> years = companyService.listYears(companyName, user.getId());
		model.addAttribute(PAGE_TITLE, "Select Year");
		model.addAttribute("years", years);
		return "year_select";
	}

	@PostMapping("/StoreCompany")
	public String processCompany(Model model, @RequestParam(value = "cid") Long cid,
			@AuthenticationPrincipal CustomUserDetails user) {

		companyService.storeSelection(user.getId(), cid);
		return "redirect:/reports";
	}

	@GetMapping("/profile")
	public String showProfile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		model.addAttribute(PAGE_TITLE, "Company Profile");
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute("company_name", cs.getCompanyName());
		model.addAttribute("address", cs.getAddress());
		model.addAttribute("year", cs.getYear());
		model.addAttribute("company", cs);
		LOGGER.info("Loading Profile...");
		return "profile";
	}

	@GetMapping("/reports")
	public String showReports(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		model.addAttribute(PAGE_TITLE, "Reports");
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return "redirect:/company_selection";
		}
		model.addAttribute("companyName", cs.getCompanyName());
		LOGGER.info("Reports Page loading...");
		return "reports";
	}

	@GetMapping("/findDaybook")
	public String findDaybook(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		/*
		 * find logged in user's company get from & to date
		 */
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute("fromdate", cs.getFromDate().format(dateFormat));
		model.addAttribute("todate", cs.getToDate().format(dateFormat));
		model.addAttribute("formdata", new DaybookForm());
		model.addAttribute(PAGE_TITLE, "CrDr Daybook");
		return "findDaybook";
	}

	@PostMapping("/daybooks")
	public String listDaybook(Model model, DaybookForm formdata, @AuthenticationPrincipal CustomUserDetails user) {
		List<DaybookView> daybookViewObj = daybookService.daybookViewRange(formdata.getStartDate(),
				formdata.getEndDate(), user.getId(), csr.findCompanyIdByUserId(user.getId()));
		if (daybookViewObj == null) 
			LOGGER.info("===========Null value======");
		
		model.addAttribute("daybookViewObj", daybookViewObj);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(formdata.getStartDate()));
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.info("Calendar Parsing Exception: Daybook Start date");
		}
		cal.add(Calendar.DAY_OF_MONTH, -1);
		try {
			model.addAttribute("openingBal",
					nf.format(closeBalRepo.findCloseBalByDate(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()),
							user.getId(), csr.findCompanyIdByUserId(user.getId()))))
					.toString();
		} catch (NullPointerException e) {
			LOGGER.error("No Close balance in databse", e);
		}
		model.addAttribute(PAGE_TITLE, "Daybook View");
		return "daybooks";
	}

	@GetMapping("/findLedger")
	public String findLedger(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute("fromdate", cs.getFromDate().format(dateFormat));
		model.addAttribute("todate", cs.getToDate().format(dateFormat));
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead(user.getId(), csr.findCompanyIdByUserId(user.getId()));
		Collections.sort(accHeadList);
		LOGGER.info("Loading Ledgers...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "CrDr Ledger");
		model.addAttribute("formdata", new LedgerForm());
		return "findLedger";
	}

	@PostMapping("/ledger")
	public String listLedger(Model model, LedgerForm ledgerForm, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.info("Ledger is Ready");
		List<LedgerView> listLedger = ledgerService.createLedgerViewList(ledgerForm, user.getId(),
				csr.findCompanyIdByUserId(user.getId()));
		model.addAttribute("listLedger", listLedger);
		model.addAttribute(PAGE_TITLE, "CrDr Ledger");
		return "ledger";
	}

	@PostMapping("/pdfLedger")
	public String ledgerPdf(Model model, LedgerForm ledgerForm, @AuthenticationPrincipal CustomUserDetails user)
			throws FileNotFoundException, JRException {
		LOGGER.info("---Printing Ledger to PDF--");
		pdfService.exportPdf(ledgerForm, user.getId(), csr.findCompanyIdByUserId(user.getId()));
		return "success";
	}

	@GetMapping("/findTrialBal")
	public String findTrial(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead(user.getId(), csr.findCompanyIdByUserId(user.getId()));
		Collections.sort(accHeadList);
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute("fromdate", cs.getFromDate().format(dateFormat));
		model.addAttribute("todate", cs.getToDate().format(dateFormat));
		LOGGER.info("Loading Trial Balance...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "Find Trial Balance");
		model.addAttribute("trialform", new TrialForm());
		return "findTrialBal";
	}

	@PostMapping("/trial")
	public String trialbal(Model model, TrialForm trialform, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.warn("Trial Balance Page Start");
		List<TrialView> listTrailView = trialBalService.createTrialBal(trialform, user.getId(),
				csr.findCompanyIdByUserId(user.getId()));
		LOGGER.info("Trial Balnce is Ready");
		model.addAttribute("listTrailView", listTrailView);
		model.addAttribute(PAGE_TITLE, "Trial Balance");
		return "trialBal";
	}

	@GetMapping("/findTradingPL")
	public String findTradingPl(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		List<AccHead> accHeadList = accHeadRepo.findAllTradingAccs(user.getId(),
				csr.findCompanyIdByUserId(user.getId()));
		Collections.sort(accHeadList);
		LOGGER.info("Loading TradingPL...");
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute("fromdate", cs.getFromDate().format(dateFormat));
		model.addAttribute("todate", cs.getToDate().format(dateFormat));
		model.addAttribute("closingStock", cs.getClosingStock());
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "Trading - Profit and Loss");
		model.addAttribute("tradingPLForm", new CommonForm());
		return "findTradingPL";
	}

	@PostMapping("/tradingPL")
	public String tradingPL(Model model, CommonForm tradingPLForm, @AuthenticationPrincipal CustomUserDetails user) {
		if (tradingPLForm.isReportOrder()) {
			List<TradingPLView> listTradingPL = tradingPLService.createTradingPL(tradingPLForm, user.getId(),
					csr.findCompanyIdByUserId(user.getId()));
			LOGGER.info("TradingPL view  is ready");
			model.addAttribute("listTradingPL", listTradingPL);
			model.addAttribute(PAGE_TITLE, "Trading - Profit & Loss");
			return "tradingPL";
		} else {
			List<List<TplBalView>> list = tradingPLService.createTradingPL2(tradingPLForm, user.getId(),
					csr.findCompanyIdByUserId(user.getId()));
			List<TplBalView> expenses = list.get(0);
			List<TplBalView> incomes = list.get(1);
			LOGGER.info("TradingPL Alt view is ready");
			model.addAttribute("expenses", expenses);
			model.addAttribute("incomes", incomes);
			model.addAttribute(PAGE_TITLE, "Trading - Profit & Loss");
			return "tradingPL_alt";
		}

	}

	@GetMapping("/findBalSheet")
	public String findBalSheet(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		List<Schedule> accHeadList = scheduleRepo.findAllAccounts(user.getId(),
				csr.findCompanyIdByUserId(user.getId()));
		Collections.sort(accHeadList);
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute("fromdate", cs.getFromDate().format(dateFormat));
		model.addAttribute("todate", cs.getToDate().format(dateFormat));
		model.addAttribute("closingStock", cs.getClosingStock());
		LOGGER.info("Loading Balance Sheet...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "Balance Sheet");
		model.addAttribute("balSheetForm", new CommonForm());
		return "findBalanceSheet";
	}

	@PostMapping("/BalanceSheet")
	public String balanceSheet(Model model, CommonForm balSheetForm, @AuthenticationPrincipal CustomUserDetails user) {
		if (balSheetForm.isReportOrder()) {
			LOGGER.info("Balance Sheet view Ready");
			List<BalanceSheetView> listBalSheet = balanceSheetService.createBalSheet(balSheetForm, user.getId(),
					csr.findCompanyIdByUserId(user.getId()));
			model.addAttribute("listBalSheet", listBalSheet);
			model.addAttribute(PAGE_TITLE, "Balance sheet");
			return "BalanceSheet";
		} else {
			LOGGER.info("Balance Sheet Alt view  Ready");
			List<List<TplBalView>> list = balanceSheetService.createBalSheet2(balSheetForm, user.getId(),
					csr.findCompanyIdByUserId(user.getId()));
			List<TplBalView> liability = list.get(0);
			List<TplBalView> asset = list.get(1);
			model.addAttribute("assets", asset);
			model.addAttribute("liabilities", liability);
			model.addAttribute(PAGE_TITLE, "Balance sheet");
			return "balancesheet_alt";
		}

	}

}
