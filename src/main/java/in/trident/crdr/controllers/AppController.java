package in.trident.crdr.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.UserRepository;

@Controller
public class AppController {

	// TODO Implement Email notification of login feature -- Spring Email
	// TODO Implement Session management via cookies

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
		ArrayList<ArrayList<AccHead>> ledgerList = accHeadRepo.showLedger(formdata);
		model.addAttribute("ledgerList", ledgerList);
		model.addAttribute("pageTitle", "CrDr Ledger");
		return "ledger";
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

}
