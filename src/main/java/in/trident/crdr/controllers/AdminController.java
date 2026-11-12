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
import org.springframework.web.bind.annotation.RequestParam;

import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.CompanyRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.RoleRepository;
import in.trident.crdr.repositories.ScheduleRepo;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.CustomUserDetails;

/**
 * The class AdminController contains controller mappings related to the url
 * passed by admin level users.
 * 
 * <p>
 * The methods here contains mappings related to creation, deletion, listing of
 * users and companies.
 * </p>
 * 
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9e
 * 
 * @since 17 jan 2022
 *
 */

@Controller
public class AdminController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
	private static final String PAGE_TITLE = "pageTitle";
	private static final String ERROR = "Error";
	private static final String SUCCESS = "success";
	private static final String MESSAGE = "message";
	private static final String DEFAULT_ROLE = "user";

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
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

	/**
	 * Controller mapping of create user workflow
	 * 
	 * @param model Model object of MVC pattern
	 * @return returns name of the html page to be sent to view resolver
	 */
	@GetMapping({ "/admin/users/new", "/create_user" })
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "registration";
	}

	/**
	 * Controller mapping of register user
	 * 
	 * @param user user object from html form/body
	 * @return returns page registration success or error
	 */
	@Transactional
	@PostMapping({ "/admin/users/new", "/process_register" })
	public String processRegister(User user, Model model) {
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Email address is required.");
			return SUCCESS;
		}
		String email = user.getEmail().trim().toLowerCase();
		if (userRepo.findByEmail(email) != null) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "An account with this email already exists.");
			return SUCCESS;
		}
		if (user.getPassword() == null || user.getPassword().length() < 8) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Password must be at least 8 characters long.");
			return SUCCESS;
		}

		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role role = roleRepo.findByRoleName(DEFAULT_ROLE).orElseGet(() -> {
			Role created = new Role();
			created.setRoleName(DEFAULT_ROLE);
			return roleRepo.save(created);
		});
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		user.setRoles(roles);
		user.setEnabled(true);
		userRepo.save(user);

		LOGGER.info("Created new user account for {}", email);
		model.addAttribute(MESSAGE, "New User created Successfully");
		model.addAttribute(PAGE_TITLE, "Registration Success");
		return "register_success";
	}

	/**
	 * Controller mapping to display list of users
	 * 
	 * @param model Model object of MVC pattern
	 * @return returns name of the list of users page to be sent to view resolver
	 */
	@GetMapping({ "/admin/users", "/users" })
	public String listUsers(Model model) {
		model.addAttribute(PAGE_TITLE, "List of Users");
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		return "users";
	}

	/**
	 * Controller mapping for delete user
	 * 
	 * @param model Model object of MVC pattern
	 * @return returns user deleted success page
	 */
	@GetMapping({ "/admin/users/delete", "/delete_user" })
	public String deleteUser(Model model) {
		model.addAttribute(PAGE_TITLE, "Delete User");
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		return "delete_users";
	}

	/**
	 * Controller mapping to delete Specific user and their companies and Data
	 * 
	 * @param user  user object from Spring security Authentication principal
	 * @param uid   user id to delete
	 * @param model Model object of MVC pattern
	 * 
	 * @return success or failure page
	 */
	@Transactional
	@PostMapping({ "/admin/users/delete", "/process_delete_user" })
	public String processDeleteUser(@AuthenticationPrincipal CustomUserDetails user,
			@RequestParam(value = "userId") Long uid, Model model) {
		if (uid == null || uid.equals(0L)) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "User Id must not be null");
		} else if (uid.equals(user.getId()) || uid.equals(9L)) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Can not delete own or Developer Accounts");
		} else {
			if (userRepo.existsById(uid)) {
				userRepo.deleteById(uid);
				daybookRepo.deleteAllByUserId(uid);
				accHeadRepo.deleteAllByUserId(uid);
				closeBalRepo.deleteAllByUserId(uid);
				companyRepo.deleteAllByUserId(uid);
				csRepo.deleteAllByUserId(uid);
				scheduleRepo.deleteAllByUserId(uid);
				model.addAttribute(PAGE_TITLE, "Success");
				model.addAttribute(MESSAGE, "User and Companies deleted Successfully");
			} else {
				model.addAttribute(PAGE_TITLE, ERROR);
				model.addAttribute(MESSAGE, "User Id does not exist");
			}
		}
		return SUCCESS;
	}

	/**
	 * Controller mapping to show delete company page
	 * 
	 * @param user  user object from Spring security Authentication principal
	 * @param model Model object of MVC pattern
	 * @return success or failure page
	 */
	@GetMapping({ "/companies/delete", "/delete_company" })
	public String deleteCompany(@AuthenticationPrincipal CustomUserDetails user, Model model) {
		model.addAttribute(PAGE_TITLE, "Delete Company");
		List<Company> companies = companyRepo.findCompaniesByUser(user.getId());
		model.addAttribute("companies", companies);
		return "delete_company";
	}

	/**
	 * Controller mapping to process delete company
	 * 
	 * @param user  user object from Spring security Authentication principal
	 * @param model Model object of MVC pattern
	 * @param cid   company id to delete
	 * @return success or failure page
	 */
	@Transactional
	@PostMapping({ "/companies/delete", "/process_delete_company" })
	public String processDeleteCompany(@AuthenticationPrincipal CustomUserDetails user, Model model,
			@RequestParam(value = "companyId") Long cid) {
		if (cid == null) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Company ID must not be null");
			return SUCCESS;
		}
		if (companyRepo.existsById(cid)) {
			companyRepo.deleteById(cid);
			if (csRepo.findCompanyByCompanyid(cid) != null) {
				csRepo.deleteAllByUserId(user.getId());
			}
			daybookRepo.deleteAllByCompanyId(cid);
			accHeadRepo.deleteAllByCompanyId(cid);
			closeBalRepo.deleteAllByCompanyId(cid);
			scheduleRepo.deleteAllByCompanyId(cid);
			model.addAttribute(PAGE_TITLE, "Company Deleted..");
			model.addAttribute(MESSAGE, "Company Deleted Sucessfully");
		} else {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Company ID does not exist");
		}
		return SUCCESS;
	}
}
