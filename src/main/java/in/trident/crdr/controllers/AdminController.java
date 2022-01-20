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

import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.UserRepository;

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
		List<User> userList = userRepo.findAll();
		model.addAttribute("userList", userList);
		return "users";
	}

	/**
	 * Delete Specific user and their companies and Data
	 * 
	 * @return Sucess or failure page
	 */
	@PostMapping("/delete_user")
	public String deleteUser(Long uid, Model model) {
		if (uid != null) {
			if (userRepo.existsById(uid)) {
				userRepo.deleteById(uid);
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

	@PostMapping("/disable_user")
	public String disableUser() {

		return "disable_user";
	}

	@PostMapping("/delete_company")
	public String deleteCompany() {
		return "delete_company";
	}
}
