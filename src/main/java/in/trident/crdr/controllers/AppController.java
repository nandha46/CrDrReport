package in.trident.crdr.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CloseBal;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.entities.User;
import in.trident.crdr.models.BalanceSheetView;
import in.trident.crdr.models.CommonForm;
import in.trident.crdr.models.DashboardAnalytics;
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
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.AccountBalanceService;
import in.trident.crdr.services.AnalyticsService;
import in.trident.crdr.services.BalanceSheetService;
import in.trident.crdr.services.CompanyService;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.services.DaybookService;
import in.trident.crdr.services.LedgerService;
import in.trident.crdr.services.PdfService;
import in.trident.crdr.services.ReportExportService;
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
	private static final String REDIRECT_COMPANY_SELECTION = "redirect:/companies/select";

	@Autowired
	private Environment env;

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

	@Autowired
	private AnalyticsService analyticsService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private DaybookRepository daybookRepo;

	@Autowired
	private AccountBalanceService accountBalanceService;

	@Autowired
	private ReportExportService reportExportService;

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute(PAGE_TITLE, "CrDr Home");
		return "index";
	}

	@GetMapping("/faq")
	public String showFaqPage(Model model) {
		model.addAttribute(PAGE_TITLE, "CrDr FAQ");
		return "faq";
	}

	@GetMapping("/docs")
	public String showDocsPage(Model model) {
		model.addAttribute(PAGE_TITLE, "CrDr Documentation");
		return "docs";
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

	@GetMapping({ "/companies/select", "/company_selection" })
	public String showCompanies(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		model.addAttribute(PAGE_TITLE, "Select Company");
		model.addAttribute("companies", companyService.listCompanies(user.getId()));
		return "company_select";
	}

	@PostMapping({ "/companies/store", "/StoreCompany" })
	public String processCompany(Model model, @RequestParam(value = "companyName") String companyName,
			@RequestParam(value = "year") Long cid, @AuthenticationPrincipal CustomUserDetails user) {
		LOGGER.info("Company Name: {}", companyName);
		if (companyName == null || companyName.trim().isEmpty()) {
			return REDIRECT_COMPANY_SELECTION;
		}
		companyService.storeSelection(user.getId(), cid);
		return "redirect:/dashboard";
	}

	@GetMapping("/profile")
	public String showProfile(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
		User dbUser = userRepo.findById(principal.getId()).orElse(principal.getUser());
		CompanySelection cs = csr.findCompanyByUser(principal.getId());
		model.addAttribute(PAGE_TITLE, "Profile");
		model.addAttribute("userEntity", dbUser);
		model.addAttribute("userName", dbUser.getFirstName() + " " + dbUser.getLastName());
		model.addAttribute("userEmail", dbUser.getEmail());
		model.addAttribute("userRoles", principal.getAuthorities());
		model.addAttribute("gravatarUrl", CustomUserDetails.gravatarUrlFor(dbUser.getEmail()));
		model.addAttribute("initials", principal.getInitials());
		if (cs != null) {
			model.addAttribute("company_name", cs.getCompanyName());
			model.addAttribute("address", cs.getAddress());
			model.addAttribute("year", cs.getYear());
			model.addAttribute("company", cs);
		}
		LOGGER.info("Loading Profile...");
		return "profile";
	}

	@PostMapping("/profile/update")
	@Transactional
	public String updateProfile(
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam(value = "avatarType", defaultValue = "initials") String avatarType,
			@RequestParam(value = "avatarValue", required = false) String avatarValue,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@AuthenticationPrincipal CustomUserDetails principal) {
		User dbUser = userRepo.findById(principal.getId())
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		String cleanFirst = firstName == null ? "" : firstName.trim();
		String cleanLast = lastName == null ? "" : lastName.trim();
		if (cleanFirst.isEmpty() || cleanLast.isEmpty()) {
			return "redirect:/profile?error=Name+is+required";
		}
		dbUser.setFirstName(cleanFirst.length() > 20 ? cleanFirst.substring(0, 20) : cleanFirst);
		dbUser.setLastName(cleanLast.length() > 20 ? cleanLast.substring(0, 20) : cleanLast);

		String safeType = "initials";
		if ("gravatar".equals(avatarType) || "icon".equals(avatarType) || "url".equals(avatarType)) {
			safeType = avatarType;
		}
		dbUser.setAvatarType(safeType);
		String safeVal = avatarValue == null ? "" : avatarValue.trim();
		if ("url".equals(safeType) && !(safeVal.startsWith("https://") || safeVal.startsWith("http://"))) {
			dbUser.setAvatarType("initials");
			dbUser.setAvatarValue(null);
		} else {
			dbUser.setAvatarValue(safeVal.length() > 255 ? safeVal.substring(0, 255) : safeVal);
		}

		if (newPassword != null && !newPassword.trim().isEmpty()) {
			if (newPassword.trim().length() < 6) {
				return "redirect:/profile?error=Password+must+be+at+least+6+characters";
			}
			dbUser.setPassword(passwordEncoder.encode(newPassword.trim()));
		}

		userRepo.save(dbUser);
		// Sync in-memory principal so navbar & subsequent requests reflect updates immediately
		principal.getUser().setFirstName(dbUser.getFirstName());
		principal.getUser().setLastName(dbUser.getLastName());
		principal.getUser().setAvatarType(dbUser.getAvatarType());
		principal.getUser().setAvatarValue(dbUser.getAvatarValue());
		return "redirect:/profile?updated=true";
	}

	@GetMapping("/settings")
	public String showSettings(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		model.addAttribute(PAGE_TITLE, "Workspace Settings");
		model.addAttribute("userName", user.getName());
		model.addAttribute("userEmail", user.getUsername());
		model.addAttribute("activeCompany", cs != null ? cs.getCompanyName() : "None selected");
		model.addAttribute("activeYear", cs != null ? cs.getYear() : "—");
		String[] profiles = env.getActiveProfiles();
		model.addAttribute("activeProfiles", profiles.length > 0 ? String.join(", ", profiles) : "default");
		String dsUrl = env.getProperty("spring.datasource.url", "");
		model.addAttribute("dbMode", dsUrl.contains(":h2:") ? "H2 Embedded (MySQL Mode)" : "MySQL");
		model.addAttribute("ddlAuto", env.getProperty("spring.jpa.hibernate.ddl-auto", "update"));
		model.addAttribute("h2ConsoleEnabled", env.getProperty("spring.h2.console.enabled", "false"));
		model.addAttribute("h2ConsolePath", env.getProperty("spring.h2.console.path", "/h2-console"));
		return "settings";
	}

	@GetMapping({ "/dashboard", "/reports" })
	public String showDashboard(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		DashboardAnalytics analytics = analyticsService.buildDashboardAnalytics(user.getId(), cs);
		model.addAttribute(PAGE_TITLE, "Analytics Dashboard");
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("analytics", analytics);
		LOGGER.info("Analytics Dashboard loading...");
		return "dashboard";
	}

	@GetMapping("/reports/menu")
	public String showReportsMenu(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		model.addAttribute(PAGE_TITLE, "Reports");
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		model.addAttribute("companyName", cs.getCompanyName());
		return "reports";
	}

	@GetMapping({ "/reports/daybook", "/findDaybook" })
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

	@PostMapping({ "/reports/daybook", "/daybooks" })
	public String listDaybook(Model model, DaybookForm formdata, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		return renderDaybookView(model, formdata, session, user);
	}

	@GetMapping("/reports/daybook/view")
	public String viewDaybookRange(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			Model model, HttpSession session, @AuthenticationPrincipal CustomUserDetails user) {
		DaybookForm form = new DaybookForm();
		DaybookForm last = (DaybookForm) session.getAttribute("lastDaybookForm");
		form.setStartDate(startDate != null ? startDate : (last != null ? last.getStartDate() : null));
		form.setEndDate(endDate != null ? endDate : (last != null ? last.getEndDate() : null));
		if (form.getStartDate() == null || form.getEndDate() == null) {
			return "redirect:/reports/daybook";
		}
		return renderDaybookView(model, form, session, user);
	}

	private String renderDaybookView(Model model, DaybookForm formdata, HttpSession session, CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		Long cid = cs.getCompanyId();
		session.setAttribute("lastDaybookForm", formdata);
		List<DaybookView> daybookViewObj = daybookService.daybookViewRange(formdata.getStartDate(),
				formdata.getEndDate(), user.getId(), cid);

		List<AccHead> accHeadList = accHeadRepo.findAllAccHead(user.getId(), cid);
		Collections.sort(accHeadList);
		model.addAttribute("accHeadList", accHeadList);
		model.addAttribute("daybookViewObj", daybookViewObj);
		model.addAttribute("startDate", formdata.getStartDate());
		model.addAttribute("endDate", formdata.getEndDate());
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("reportPeriod", formdata.getStartDate() + " to " + formdata.getEndDate());
		LocalDate startDate = FormatUtil.parseIsoDate(formdata.getStartDate());
		String prevDate = startDate.minusDays(1).format(FormatUtil.ISO_DATE);
		Double openingBal = closeBalRepo.findCloseBalByDate(prevDate, user.getId(), cid);
		model.addAttribute("openingBal", FormatUtil.formatAmount(FormatUtil.nvlDouble(openingBal)));
		model.addAttribute(PAGE_TITLE, "Daybook View");
		return "daybooks";
	}

	@PostMapping("/reports/daybook/entry")
	@Transactional
	public String addDaybookEntry(@RequestParam("date") String date,
			@RequestParam("acccode") int acccode,
			@RequestParam("narration") String narration,
			@RequestParam(value = "drAmt", defaultValue = "0") double drAmt,
			@RequestParam(value = "crAmt", defaultValue = "0") double crAmt,
			@RequestParam(value = "stkValue", defaultValue = "0") int stkValue,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		String isoDate = FormatUtil.parseIsoDate(date).format(FormatUtil.ISO_DATE);
		List<Daybook> existing = daybookRepo.findDaybookByDate(isoDate, user.getId(), cid);
		int nextSno = existing.stream().mapToInt(Daybook::getsNo).max().orElse(0) + 1;

		Daybook entry = new Daybook();
		entry.setsNo(nextSno);
		entry.setDate(isoDate);
		entry.setAcccode(acccode);
		entry.setNarration(narration == null ? "" : narration.trim());
		entry.setDrAmt(Math.max(0.0, drAmt));
		entry.setCrAmt(Math.max(0.0, crAmt));
		entry.setSktValue(stkValue);
		entry.setUserid(user.getId());
		entry.setCompanyid(cid);
		daybookRepo.save(entry);
		recomputeCloseBalForDate(isoDate, user.getId(), cid);

		String s = (startDate != null && !startDate.isEmpty()) ? startDate : isoDate;
		String e = (endDate != null && !endDate.isEmpty()) ? endDate : isoDate;
		return "redirect:/reports/daybook/view?startDate=" + s + "&endDate=" + e;
	}

	@PostMapping("/reports/daybook/entry/{id}")
	@ResponseBody
	@Transactional
	public Map<String, Object> updateDaybookEntry(@PathVariable("id") Long id,
			@RequestParam("date") String date,
			@RequestParam("acccode") int acccode,
			@RequestParam("narration") String narration,
			@RequestParam(value = "drAmt", defaultValue = "0") double drAmt,
			@RequestParam(value = "crAmt", defaultValue = "0") double crAmt,
			@AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		Daybook entry = daybookRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Daybook entry not found"));
		if (!user.getId().equals(entry.getUserid()) || !cid.equals(entry.getCompanyid())) {
			throw new IllegalArgumentException("Access denied for daybook entry");
		}
		String oldDate = entry.getDate();
		String isoDate = FormatUtil.parseIsoDate(date).format(FormatUtil.ISO_DATE);
		entry.setDate(isoDate);
		entry.setAcccode(acccode);
		entry.setNarration(narration == null ? "" : narration.trim());
		entry.setDrAmt(Math.max(0.0, drAmt));
		entry.setCrAmt(Math.max(0.0, crAmt));
		daybookRepo.save(entry);
		recomputeCloseBalForDate(oldDate, user.getId(), cid);
		if (!isoDate.equals(oldDate)) {
			recomputeCloseBalForDate(isoDate, user.getId(), cid);
		}
		Map<String, Object> res = new HashMap<>();
		res.put("ok", true);
		res.put("id", entry.getDbId());
		return res;
	}

	@PostMapping("/reports/daybook/entry/{id}/delete")
	@ResponseBody
	@Transactional
	public Map<String, Object> deleteDaybookEntry(@PathVariable("id") Long id,
			@AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		Daybook entry = daybookRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Daybook entry not found"));
		if (!user.getId().equals(entry.getUserid()) || !cid.equals(entry.getCompanyid())) {
			throw new IllegalArgumentException("Access denied for daybook entry");
		}
		String isoDate = entry.getDate();
		daybookRepo.delete(entry);
		recomputeCloseBalForDate(isoDate, user.getId(), cid);
		Map<String, Object> res = new HashMap<>();
		res.put("ok", true);
		return res;
	}

	private void recomputeCloseBalForDate(String isoDate, Long uid, Long cid) {
		if (isoDate == null || isoDate.isEmpty()) {
			return;
		}
		LocalDate parsed = FormatUtil.parseIsoDate(isoDate);
		String norm = parsed.format(FormatUtil.ISO_DATE);
		List<Daybook> entries = daybookRepo.findDaybookByDate(norm, uid, cid);
		double drTot = 0.0;
		double crTot = 0.0;
		for (Daybook d : entries) {
			drTot += d.getDrAmt() != null ? d.getDrAmt() : 0.0;
			crTot += d.getCrAmt() != null ? d.getCrAmt() : 0.0;
		}
		double prevClose = accountBalanceService.resolveCashOnHand(parsed.minusDays(1).format(FormatUtil.ISO_DATE), uid, cid);
		List<CloseBal> existing = closeBalRepo.findCloseBalList(norm, norm, uid, cid);
		CloseBal cb = (existing != null && !existing.isEmpty()) ? existing.get(0) : new CloseBal();
		cb.setDate(norm);
		cb.setUserid(uid);
		cb.setComapnyid(cid);
		cb.setDrTot(drTot);
		cb.setCrTot(crTot);
		cb.setCloseBal(prevClose + drTot - crTot);
		closeBalRepo.save(cb);
	}

	@GetMapping({ "/reports/ledger", "/findLedger" })
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

	@PostMapping({ "/reports/ledger", "/ledger" })
	public String listLedger(Model model, LedgerForm ledgerForm, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		session.setAttribute("lastLedgerForm", ledgerForm);
		LOGGER.info("Ledger is Ready");
		List<LedgerView> listLedger = ledgerService.createLedgerViewList(ledgerForm, user.getId(), cs.getCompanyId());
		model.addAttribute("listLedger", listLedger);
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("reportPeriod", ledgerForm.getStartDate() + " to " + ledgerForm.getEndDate());
		model.addAttribute(PAGE_TITLE, "CrDr Ledger");
		return "ledger";
	}

	@PostMapping({ "/reports/ledger/pdf", "/pdfLedger" })
	public String ledgerPdf(Model model, LedgerForm ledgerForm, @AuthenticationPrincipal CustomUserDetails user)
			throws IOException, JRException {
		Long cid = requireCompanyId(user.getId());
		LOGGER.info("---Printing Ledger to PDF--");
		String outputPath = pdfService.exportPdf(ledgerForm, user.getId(), cid);
		model.addAttribute(PAGE_TITLE, "PDF Generated");
		model.addAttribute("message", "Ledger PDF generated at: " + outputPath);
		return "success";
	}

	@GetMapping({ "/reports/trial-balance", "/findTrialBal" })
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

	@PostMapping({ "/reports/trial-balance", "/trial" })
	public String trialbal(Model model, TrialForm trialform, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		session.setAttribute("lastTrialForm", trialform);
		LOGGER.warn("Trial Balance Page Start");
		List<TrialView> listTrailView = trialBalService.createTrialBal(trialform, user.getId(), cs.getCompanyId());
		LOGGER.info("Trial Balance is Ready");
		model.addAttribute("listTrailView", listTrailView);
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("reportPeriod", "As on " + trialform.getEndDate());
		model.addAttribute(PAGE_TITLE, "Trial Balance");
		return "trialBal";
	}

	@GetMapping({ "/reports/trading-pl", "/findTradingPL" })
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

	@PostMapping({ "/reports/trading-pl", "/tradingPL" })
	public String tradingPL(Model model, CommonForm tradingPLForm, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		session.setAttribute("lastTradingPlForm", tradingPLForm);
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("reportPeriod", "As on " + tradingPLForm.getEndDate());
		if (tradingPLForm.isReportOrder()) {
			List<TradingPLView> listTradingPL = tradingPLService.createTradingPL(tradingPLForm, user.getId(), cs.getCompanyId());
			LOGGER.info("TradingPL view is ready");
			model.addAttribute("listTradingPL", listTradingPL);
			model.addAttribute(PAGE_TITLE, "Trading - Profit & Loss");
			return "tradingPL";
		} else {
			List<List<TplBalView>> list = tradingPLService.createTradingPL2(tradingPLForm, user.getId(), cs.getCompanyId());
			model.addAttribute("expenses", list.get(0));
			model.addAttribute("incomes", list.get(1));
			LOGGER.info("TradingPL Alt view is ready");
			model.addAttribute(PAGE_TITLE, "Trading - Profit & Loss");
			return "tradingPL_alt";
		}
	}

	@GetMapping({ "/reports/balance-sheet", "/findBalSheet" })
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

	@PostMapping({ "/reports/balance-sheet", "/BalanceSheet" })
	public String balanceSheet(Model model, CommonForm balSheetForm, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		session.setAttribute("lastBalSheetForm", balSheetForm);
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("reportPeriod", "As on " + balSheetForm.getEndDate());
		if (balSheetForm.isReportOrder()) {
			LOGGER.info("Balance Sheet view Ready");
			List<BalanceSheetView> listBalSheet = balanceSheetService.createBalSheet(balSheetForm, user.getId(), cs.getCompanyId());
			model.addAttribute("listBalSheet", listBalSheet);
			model.addAttribute(PAGE_TITLE, "Balance sheet");
			return "BalanceSheet";
		} else {
			LOGGER.info("Balance Sheet Alt view Ready");
			List<List<TplBalView>> list = balanceSheetService.createBalSheet2(balSheetForm, user.getId(), cs.getCompanyId());
			model.addAttribute("liabilities", list.get(0));
			model.addAttribute("assets", list.get(1));
			model.addAttribute(PAGE_TITLE, "Balance sheet");
			return "balancesheet_alt";
		}
	}

	@GetMapping("/accounts")
	public String listAccounts(Model model, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			return REDIRECT_COMPANY_SELECTION;
		}
		List<AccHead> accHeadList = accHeadRepo.findAllAccHead(user.getId(), cs.getCompanyId());
		Collections.sort(accHeadList);
		model.addAttribute(PAGE_TITLE, "Account Heads");
		model.addAttribute("companyName", cs.getCompanyName());
		model.addAttribute("accHeadList", accHeadList);
		return "accounts";
	}

	@PostMapping("/accounts/save")
	@Transactional
	public String saveAccountHead(@RequestParam("accCode") int accCode,
			@RequestParam("accName") String accName,
			@RequestParam(value = "shortName", required = false) String shortName,
			@RequestParam("accType") String accType,
			@RequestParam(value = "drAmt", defaultValue = "0") double drAmt,
			@RequestParam(value = "crAmt", defaultValue = "0") double crAmt,
			@RequestParam(value = "level1", defaultValue = "2") int level1,
			@RequestParam(value = "orderCode", defaultValue = "1") int orderCode,
			@AuthenticationPrincipal CustomUserDetails user) {
		Long cid = requireCompanyId(user.getId());
		List<AccHead> existing = accHeadRepo.findAllAccHead(user.getId(), cid);
		AccHead head = existing.stream().filter(a -> a.getAccCode() == accCode).findFirst().orElse(null);
		if (head == null) {
			head = new AccHead();
			head.setAccCode(accCode);
			head.setUserid(user.getId());
			head.setCompanyid(cid);
			head.setsNo(existing.size() + 1);
			head.setUserStatus(true);
			head.setStkNeeded(false);
			head.setStkStatus("N");
		}
		head.setAccName(accName == null ? "" : accName.trim());
		String sn = (shortName == null || shortName.trim().isEmpty()) ? head.getAccName() : shortName.trim();
		head.setShortName(sn);
		head.setAccType(accType == null ? "A" : accType.trim());
		head.setDrAmt(Math.max(0.0, drAmt));
		head.setCrAmt(Math.max(0.0, crAmt));
		head.setLevel1(level1);
		head.setOrderCode(orderCode);
		accHeadRepo.save(head);
		return "redirect:/accounts?saved=true";
	}

	@GetMapping("/reports/daybook/export/{format}")
	public ResponseEntity<byte[]> exportDaybook(@PathVariable("format") String format,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			HttpSession session, @AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			throw new IllegalArgumentException("Please select a company first.");
		}
		DaybookForm last = (DaybookForm) session.getAttribute("lastDaybookForm");
		String s = startDate != null ? startDate : (last != null ? last.getStartDate() : cs.getFromDate().format(FormatUtil.ISO_DATE));
		String e = endDate != null ? endDate : (last != null ? last.getEndDate() : cs.getToDate().format(FormatUtil.ISO_DATE));
		List<DaybookView> views = daybookService.daybookViewRange(s, e, user.getId(), cs.getCompanyId());
		String prevDate = FormatUtil.parseIsoDate(s).minusDays(1).format(FormatUtil.ISO_DATE);
		String openingBal = FormatUtil.formatAmount(FormatUtil.nvlDouble(closeBalRepo.findCloseBalByDate(prevDate, user.getId(), cs.getCompanyId())));
		String period = s + " to " + e;
		if ("excel".equalsIgnoreCase(format)) {
			return attachment(reportExportService.exportDaybookExcel(cs.getCompanyName(), period, openingBal, views),
					"daybook-" + s + ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
		return attachment(reportExportService.exportDaybookPdf(cs.getCompanyName(), period, openingBal, views),
				"daybook-" + s + ".pdf", MediaType.APPLICATION_PDF_VALUE);
	}

	@GetMapping("/reports/ledger/export/{format}")
	public ResponseEntity<byte[]> exportLedger(@PathVariable("format") String format, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			throw new IllegalArgumentException("Please select a company first.");
		}
		LedgerForm form = (LedgerForm) session.getAttribute("lastLedgerForm");
		if (form == null) {
			throw new IllegalArgumentException("Generate a Ledger report first before exporting.");
		}
		List<LedgerView> rows = ledgerService.createLedgerViewList(form, user.getId(), cs.getCompanyId());
		String period = form.getStartDate() + " to " + form.getEndDate();
		if ("excel".equalsIgnoreCase(format)) {
			return attachment(reportExportService.exportLedgerExcel(cs.getCompanyName(), period, rows),
					"ledger-" + form.getStartDate() + ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
		return attachment(reportExportService.exportLedgerPdf(cs.getCompanyName(), period, rows),
				"ledger-" + form.getStartDate() + ".pdf", MediaType.APPLICATION_PDF_VALUE);
	}

	@GetMapping("/reports/trial-balance/export/{format}")
	public ResponseEntity<byte[]> exportTrialBalance(@PathVariable("format") String format, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			throw new IllegalArgumentException("Please select a company first.");
		}
		TrialForm form = (TrialForm) session.getAttribute("lastTrialForm");
		if (form == null) {
			throw new IllegalArgumentException("Generate a Trial Balance report first before exporting.");
		}
		List<TrialView> rows = trialBalService.createTrialBal(form, user.getId(), cs.getCompanyId());
		String period = "As on " + form.getEndDate();
		if ("excel".equalsIgnoreCase(format)) {
			return attachment(reportExportService.exportTrialBalanceExcel(cs.getCompanyName(), period, rows),
					"trial-balance-" + form.getEndDate() + ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
		return attachment(reportExportService.exportTrialBalancePdf(cs.getCompanyName(), period, rows),
				"trial-balance-" + form.getEndDate() + ".pdf", MediaType.APPLICATION_PDF_VALUE);
	}

	@GetMapping("/reports/trading-pl/export/{format}")
	public ResponseEntity<byte[]> exportTradingPl(@PathVariable("format") String format, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			throw new IllegalArgumentException("Please select a company first.");
		}
		CommonForm form = (CommonForm) session.getAttribute("lastTradingPlForm");
		if (form == null) {
			throw new IllegalArgumentException("Generate a Trading & P&L report first before exporting.");
		}
		List<TradingPLView> rows = tradingPLService.createTradingPL(form, user.getId(), cs.getCompanyId());
		String period = "As on " + form.getEndDate();
		if ("excel".equalsIgnoreCase(format)) {
			return attachment(reportExportService.exportTradingPlExcel(cs.getCompanyName(), period, rows),
					"trading-pl-" + form.getEndDate() + ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
		return attachment(reportExportService.exportTradingPlPdf(cs.getCompanyName(), period, rows),
				"trading-pl-" + form.getEndDate() + ".pdf", MediaType.APPLICATION_PDF_VALUE);
	}

	@GetMapping("/reports/balance-sheet/export/{format}")
	public ResponseEntity<byte[]> exportBalanceSheet(@PathVariable("format") String format, HttpSession session,
			@AuthenticationPrincipal CustomUserDetails user) {
		CompanySelection cs = csr.findCompanyByUser(user.getId());
		if (cs == null) {
			throw new IllegalArgumentException("Please select a company first.");
		}
		CommonForm form = (CommonForm) session.getAttribute("lastBalSheetForm");
		if (form == null) {
			throw new IllegalArgumentException("Generate a Balance Sheet report first before exporting.");
		}
		List<BalanceSheetView> rows = balanceSheetService.createBalSheet(form, user.getId(), cs.getCompanyId());
		String period = "As on " + form.getEndDate();
		if ("excel".equalsIgnoreCase(format)) {
			return attachment(reportExportService.exportBalanceSheetExcel(cs.getCompanyName(), period, rows),
					"balance-sheet-" + form.getEndDate() + ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
		return attachment(reportExportService.exportBalanceSheetPdf(cs.getCompanyName(), period, rows),
				"balance-sheet-" + form.getEndDate() + ".pdf", MediaType.APPLICATION_PDF_VALUE);
	}

	private ResponseEntity<byte[]> attachment(byte[] data, String filename, String contentType) {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.parseMediaType(contentType))
				.body(data);
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
