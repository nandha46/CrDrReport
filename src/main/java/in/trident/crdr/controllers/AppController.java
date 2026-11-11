package in.trident.crdr.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.DaybookForm;
import in.trident.crdr.models.DaybookView;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;
import in.trident.crdr.models.TplBalView;
import in.trident.crdr.models.TradingPLView;
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
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
import in.trident.crdr.util.FormatUtil;
import net.sf.jasperreports.engine.JRException;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since day 1
 * @version 0.0.9e
 */
@Controller
public class AppController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

	private static final String PAGE_TITLE = "pageTitle";
	private static final String REDIRECT_COMPANY_SELECTION = "redirect:/company_selection";

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

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute(PAGE_TITLE, "CrDr Home");
		return "index";
	}

	@GetMapping("/403")
	public String accessDenied(Model model) {
		model.addAttribute(PAGE_TITLE, "Access Denied");
		return "error/403";
	}

	@GetMapping("/login")
	public String showlogin(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			LOGGER.info("Anonymous user");
			return "login";
		}
		LOGGER.info("Already logged in");
		return REDIRECT_COMPANY_SELECTION;
	}

	@GetMapping("/company_selection")
	public String showCompanies(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		model.addAttribute(PAGE_TITLE, "Select Company");
		model.addAttribute("companies", companyService.listCompanies(user.getId()));
		return "company_select";
	}

	@PostMapping("/StoreCompany")
	public String processCompany(Model model, @RequestParam(value = "companyName") String companyName,
			@RequestParam(value = "year") Long cid, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.info("Company Name: {}", companyName);
		if (companyName == null || companyName.trim().isEmpty()) {
			return REDIRECT_COMPANY_SELECTION;
		}
		companyService.storeSelection(user.getId(), cid);
		return "redirect:/reports";
	}

	@GetMapping("/profile")
	public String showProfile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		model.addAttribute(PAGE_TITLE, "Company Profile");
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
			return REDIRECT_COMPANY_SELECTION;
		}
		model.addAttribute("companyName", cs.getCompanyName());
		LOGGER.info("Reports Page loading...");
		return "reports";
	}

	@GetMapping("/findDaybook")
	public String findDaybook(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		populateDateBounds(model, cs);
		model.addAttribute("formdata", new DaybookForm());
		model.addAttribute(PAGE_TITLE, "CrDr Daybook");
		return "findDaybook";
	}

	@PostMapping("/daybooks")
	public String listDaybook(Model model, DaybookForm formdata, @AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		List<DaybookView> daybookViewObj = daybookService.daybookViewRange(formdata.getStartDate(),
				formdata.getEndDate(), user.getId(), cid);

		model.addAttribute("daybookViewObj", daybookViewObj);
		LocalDate startDate = FormatUtil.parseIsoDate(formdata.getStartDate());
		String prevDate = startDate.minusDays(1).format(FormatUtil.ISO_DATE);
		Double openingBal = closeBalRepo.findCloseBalByDate(prevDate, user.getId(), cid);
		model.addAttribute("openingBal", FormatUtil.formatAmount(FormatUtil.nvlDouble(openingBal)));
		model.addAttribute(PAGE_TITLE, "Daybook View");
		return "daybooks";
	}

	@GetMapping("/findLedger")
	public String findLedger(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		populateDateBounds(model, cs);
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead(user.getId(), cs.getCompanyId());
		Collections.sort(accHeadList);
		LOGGER.info("Loading Ledgers...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "CrDr Ledger");
		model.addAttribute("formdata", new LedgerForm());
		return "findLedger";
	}

	@PostMapping("/ledger")
	public String listLedger(Model model, LedgerForm ledgerForm, @AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		LOGGER.info("Ledger is Ready");
		List<LedgerView> listLedger = ledgerService.createLedgerViewList(ledgerForm, user.getId(), cid);
		model.addAttribute("listLedger", listLedger);
		model.addAttribute(PAGE_TITLE, "CrDr Ledger");
		return "ledger";
	}

	@PostMapping("/pdfLedger")
	public String ledgerPdf(Model model, LedgerForm ledgerForm, @AuthenticationPrincipal CustomUserDetails user)
			throws IOException, JRException {
		Long cid = requireCompanyId(user.getId());
		LOGGER.info("---Printing Ledger to PDF--");
		String outputPath = pdfService.exportPdf(ledgerForm, user.getId(), cid);
		model.addAttribute(PAGE_TITLE, "PDF Generated");
		model.addAttribute("message", "Ledger PDF generated at: " + outputPath);
		return "success";
	}

	@GetMapping("/findTrialBal")
	public String findTrial(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead(user.getId(), cs.getCompanyId());
		Collections.sort(accHeadList);
		populateDateBounds(model, cs);
		LOGGER.info("Loading Trial Balance...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "Find Trial Balance");
		model.addAttribute("trialform", new TrialForm());
		return "findTrialBal";
	}

	@PostMapping("/trial")
	public String trialbal(Model model, TrialForm trialform, @AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		LOGGER.warn("Trial Balance Page Start");
		List<TrialView> listTrailView = trialBalService.createTrialBal(trialform, user.getId(), cid);
		LOGGER.info("Trial Balance is Ready");
		model.addAttribute("listTrailView", listTrailView);
		model.addAttribute(PAGE_TITLE, "Trial Balance");
		return "trialBal";
	}

	@GetMapping("/findTradingPL")
	public String findTradingPl(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		List<AccHead> accHeadList = accHeadRepo.findAllTradingAccs(user.getId(), cs.getCompanyId());
		Collections.sort(accHeadList);
		LOGGER.info("Loading TradingPL...");
		populateDateBounds(model, cs);
		model.addAttribute("closingStock", cs.getClosingStock());
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "Trading - Profit and Loss");
		model.addAttribute("tradingPLForm", new CommonForm());
		return "findTradingPL";
	}

	@PostMapping("/tradingPL")
	public String tradingPL(Model model, CommonForm tradingPLForm, @AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		if (tradingPLForm.isReportOrder()) {
			List<TradingPLView> listTradingPL = tradingPLService.createTradingPL(tradingPLForm, user.getId(), cid);
			LOGGER.info("TradingPL view is ready");
			model.addAttribute("listTradingPL", listTradingPL);
			model.addAttribute(PAGE_TITLE, "Trading - Profit & Loss");
			return "tradingPL";
		} else {
			List<List<TplBalView>> list = tradingPLService.createTradingPL2(tradingPLForm, user.getId(), cid);
			model.addAttribute("expenses", list.get(0));
			model.addAttribute("incomes", list.get(1));
			LOGGER.info("TradingPL Alt view is ready");
			model.addAttribute(PAGE_TITLE, "Trading - Profit & Loss");
			return "tradingPL_alt";
		}
	}

	@GetMapping("/findBalSheet")
	public String findBalSheet(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		List<Schedule> accHeadList = scheduleRepo.findAllAccounts(user.getId(), cs.getCompanyId());
		Collections.sort(accHeadList);
		populateDateBounds(model, cs);
		model.addAttribute("closingStock", cs.getClosingStock());
		LOGGER.info("Loading Balance Sheet...");
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute(PAGE_TITLE, "Balance Sheet");
		model.addAttribute("balSheetForm", new CommonForm());
		return "findBalanceSheet";
	}

	@PostMapping("/BalanceSheet")
	public String balanceSheet(Model model, CommonForm balSheetForm, @AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		if (balSheetForm.isReportOrder()) {
			LOGGER.info("Balance Sheet view Ready");
			List<BalanceSheetView> listBalSheet = balanceSheetService.createBalSheet(balSheetForm, user.getId(), cid);
			model.addAttribute("listBalSheet", listBalSheet);
			model.addAttribute(PAGE_TITLE, "Balance sheet");
			return "BalanceSheet";
		} else {
			LOGGER.info("Balance Sheet Alt view Ready");
			List<List<TplBalView>> list = balanceSheetService.createBalSheet2(balSheetForm, user.getId(), cid);
			model.addAttribute("liabilities", list.get(0));
			model.addAttribute("assets", list.get(1));
			model.addAttribute(PAGE_TITLE, "Balance sheet");
			return "balancesheet_alt";
		}
	}

	private void populateDateBounds(Model model, CompanySelection cs) {
		if (cs.getFromDate() != null) {
			model.addAttribute("fromdate", cs.getFromDate().format(FormatUtil.ISO_DATE));
		}
		if (cs.getToDate() != null) {
			model.addAttribute("todate", cs.getToDate().format(FormatUtil.ISO_DATE));
		}
	}

	private Long requireCompanyId(Long userId) {
		Long cid = csr.findCompanyIdByUserId(userId);
		if (cid == null) {
			throw new IllegalArgumentException("Please select a company before generating reports.");
		}
		return cid;
	}
}
