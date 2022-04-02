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
	private static final String PAGE_TITLE = "pageTitle";
	private static final String ERROR = "Error";
	private static final String SUCCESS = "success";
	private static final String MESSAGE = "message";

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

	/**
	 * Controller mapping of create user workflow
	 * 
	 * @param model Model object of MVC pattern
	 * @return returns name of the html page to be sent to view resolver
	 */
	@GetMapping("/create_user")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "signup_form";
	}

	
	/**
	 * Controller mapping of register user
	 * 
	 * @param user user object from html form/body
	 * @return returns page registration sucess
	 */
	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
		String encodedPass = passEncoder.encode(user.getPassword());
		LOGGER.info("Password is {}", user.getPassword());
		user.setPassword(encodedPass);
		Set<Role> roles = new HashSet<>();
		Role role = new Role();
		role.setRoleName("user");
		roles.add(role);
		user.setRoles(roles);
		userRepo.save(user);
		return "register_success";
	}

	/**
	 * Controller mapping to display list of users
	 * 
	 * @param model Model object of MVC pattern
	 * @return returns name of the list of users page to be sent to view resolver
	 */
	@GetMapping("/users")
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
	 * @return returns user deleted suceess page
	 */
	@GetMapping("/delete_user")
	public String deleteUser(Model model) {
		model.addAttribute(PAGE_TITLE, "Delete User");
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		return "delete_users";
	}

	/**
	 * Controller mapping to delete Specific user and their companies and Data
	 * 
	 * @param user     user object from Spring security Authentication pricipal
	 * @param formdata data object from html form
	 * @param model    Model object of MVC pattern
	 * 
	 * @return success or failure page
	 */
	@Transactional
	@PostMapping("/process_delete_user")
	public String processDeleteUser(@AuthenticationPrincipal CustomUserDetails user,
			@RequestParam(value = "uid") Long uid, Model model) {
		if (uid.equals(user.getId()) || uid.equals(9l)) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Can not delete own or Developer Accounts");
			return SUCCESS;
		} else if (uid == 0l) {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "User Id must not be null");
			return SUCCESS;
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
				return SUCCESS;
			} else {
				model.addAttribute(PAGE_TITLE, ERROR);
				model.addAttribute(MESSAGE, "User Id does not exist");
				return SUCCESS;
			}

		}
	}

	/**
	 * Controller mapping to show delete company page
	 * 
	 * @param user  user object from Spring security Authentication pricipal
	 * @param model Model object of MVC pattern
	 * @return success or failure page
	 */

	@GetMapping("/delete_company")
	public String deleteCompany(@AuthenticationPrincipal CustomUserDetails user, Model model) {
		model.addAttribute(PAGE_TITLE, "Delete Company");
		List<Company> companies = companyRepo.findCompaniesByUser(user.getId());
		model.addAttribute("companies", companies);
		return "delete_company";
	}

	/**
	 * Controller mapping to process delete company
	 * 
	 * @param user      user object from Spring security Authentication pricipal
	 * @param model     Model object of MVC pattern
	 * @param adminForm data from html form
	 * @return success or failure page
	 */
	@Transactional
	@PostMapping("/process_delete_company")
	public String processDeleteCompany(@AuthenticationPrincipal CustomUserDetails user, Model model,
			@RequestParam(value = "cid") Long cid) {
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
			return SUCCESS;
		} else {
			model.addAttribute(PAGE_TITLE, ERROR);
			model.addAttribute(MESSAGE, "Company ID does not exist");
			return SUCCESS;
		}
	}
}
