package in.trident.crdr.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.models.AdminForm;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.CompanyRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.CustomUserDetails;

/**
 * 
 * @author Nandhakumar Subramanina
 * 
 * @version 0.0.9d
 * 
 * @since 17 jan 2022
 *
 */

@Controller
public class AdminController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private DaybookRepository daybookRepo;
	@Autowired
	private AccHeadRepo accHeadRepo;
	@Autowired
	private CloseBalRepo closeBalRepo;
	@Autowired
	private CompanyRepo companyRepo;
	@Autowired
	private CompSelectionRepo csRepo;
	@Autowired
	private ScheduleRepo scheduleRepo;

	@GetMapping("/create_user")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "signup_form";
	}

	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
		String encodedPass = passEncoder.encode(user.getPassword());
		LOGGER.info("Password is : " + user.getPassword());
		user.setPassword(encodedPass);
		Set<Role> roles = new HashSet<Role>();
		Role role = new Role();
		// TODO Add another field in User object named role or pass it using JS
		role.setRoleName("user");
		roles.add(role);
		user.setRoles(roles);
		userRepo.save(user);
		// TODO Check if user already exists by email id
		return "register_success";
	}

	@GetMapping("/users")
	public String listUsers(Model model) {
		model.addAttribute("pageTitle", "List of Users");
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		return "users";
	}

	@GetMapping("/delete_user")
	public String deleteUser(Model model) {
		model.addAttribute("pageTitle", "Delete User");
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		model.addAttribute("adminForm", new AdminForm());
		return "delete_users";
	}

	/**
	 * Delete Specific user and their companies and Data
	 * 
	 * @return Sucess or failure page
	 */
	@Transactional
	@PostMapping("/process_delete_user")
	public String processDeleteUser(@AuthenticationPrincipal CustomUserDetails user, AdminForm formdata, Model model) {
		Long uid = formdata.getUserId();
		if (uid == user.getId() || uid == 9d) {
			model.addAttribute("pageTitle", "Error");
			model.addAttribute("message", "Can not delete own or Developer Accounts");
			return "success";
		}
		if (uid != null) {
			if (userRepo.existsById(uid)) {
				userRepo.deleteById(uid);
				daybookRepo.deleteAllByUserId(uid);
				accHeadRepo.deleteAllByUserId(uid);
				closeBalRepo.deleteAllByUserId(uid);
				companyRepo.deleteAllByUserId(uid);
				csRepo.deleteAllByUserId(uid);
				scheduleRepo.deleteAllByUserId(uid);
				model.addAttribute("pageTitle", "Success");
				model.addAttribute("message", "User and Companies deleted Successfully");
				return "success";
			} else {
				model.addAttribute("pageTitle", "Error");
				model.addAttribute("message", "User Id does not exist");
				return "success";
			}
		} else {
			model.addAttribute("pageTitle", "Error");
			model.addAttribute("message", "User Id must not be null");
			return "success";
		}
	}

	@GetMapping("/delete_company")
	public String deleteCompany(@AuthenticationPrincipal CustomUserDetails user, Model model) {
		model.addAttribute("pageTitle", "Delete Company");
		List<Company> companies = companyRepo.findCompaniesByUser(user.getId());
		model.addAttribute("companies", companies);
		model.addAttribute("adminForm", new AdminForm());
		return "delete_company";
	}

	@Transactional
	@PostMapping("/process_delete_company")
	public String processDeleteCompany(@AuthenticationPrincipal CustomUserDetails user, Model model,
			AdminForm adminForm) {
		if (companyRepo.existsById(adminForm.getCompanyId())) {
			companyRepo.deleteById(adminForm.getCompanyId());
			if (csRepo.findCompanyByCompanyid(adminForm.getCompanyId()) != null) {
				csRepo.deleteAllByUserId(user.getId());
			}
			daybookRepo.deleteAllByCompanyId(adminForm.getCompanyId());
			accHeadRepo.deleteAllByCompanyId(adminForm.getCompanyId());
			closeBalRepo.deleteAllByCompanyId(adminForm.getCompanyId());
			scheduleRepo.deleteAllByCompanyId(adminForm.getCompanyId());
			model.addAttribute("pageTitle", "Company Deleted..");
			model.addAttribute("message", "Company Deleted Sucessfully");
			return "success";
		} else {
			model.addAttribute("pageTitle", "Error");
			model.addAttribute("message", "Company ID does not exist");
			return "success";
		}
	}
}
