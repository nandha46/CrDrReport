package in.trident.crdr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.PagePerformanceMetricRepo;
import in.trident.crdr.repositories.RoleRepository;
import in.trident.crdr.repositories.UserRepository;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.util.CSVUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CSVAndSecurityTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PagePerformanceMetricRepo pagePerformanceMetricRepo;

	private CustomUserDetails buildPrincipal(String roleName, Long id) {
		Role role = new Role();
		role.setRoleName(roleName);
		User u = new User();
		u.setId(id);
		u.setEmail(roleName + "@example.com");
		u.setFirstName("Test");
		u.setLastName("User");
		u.setPassword("encoded");
		u.setEnabled(true);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		u.setRoles(roles);
		return new CustomUserDetails(u);
	}

	@Test
	public void csvUtilParsesHeadersAndAcceptsWindowsCsvMimeType() {
		String csv = "Sno,TDate,Narration,acccode,dramt,cramt,stkvalue\n"
				+ "1,2020-04-01,Opening Sale,401,0,1250.50,0\n";
		MockMultipartFile file = new MockMultipartFile("file1", "daybook.csv", "application/vnd.ms-excel",
				csv.getBytes(StandardCharsets.UTF_8));

		assertThat(CSVUtil.hasCSVFormat(file)).isTrue();
		List<Daybook> rows = CSVUtil.csvToDaybook(7L, 12L, new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
		assertThat(rows).hasSize(1);
		assertThat(rows.get(0).getNarration()).isEqualTo("Opening Sale");
		assertThat(rows.get(0).getCompanyid()).isEqualTo(12L);
	}

	@Test
	public void publicAndProtectedRoutesEnforceSecurityRules() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
		mockMvc.perform(get("/faq")).andExpect(status().isOk()).andExpect(view().name("faq"));
		mockMvc.perform(get("/docs")).andExpect(status().isOk()).andExpect(view().name("docs"));
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
		mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());

		// Anonymous requests to protected paths redirect to login
		mockMvc.perform(get("/reports")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
		mockMvc.perform(get("/create_user")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));

		// Plain user is allowed on standard /csv/upload, /settings, and legacy /uploadcsv,
		// but forbidden on /admin/users/new & /create_user & /process_register
		CustomUserDetails standardUser = buildPrincipal("user", 2L);
		mockMvc.perform(get("/csv/upload").with(user(standardUser))).andExpect(status().isOk())
				.andExpect(view().name("uploadCSV"));
		mockMvc.perform(get("/uploadcsv").with(user(standardUser))).andExpect(status().isOk())
				.andExpect(view().name("uploadCSV"));
		mockMvc.perform(get("/settings").with(user(standardUser))).andExpect(status().isOk())
				.andExpect(view().name("settings"));
		mockMvc.perform(get("/admin/users/new").with(user(standardUser))).andExpect(status().isForbidden());
		mockMvc.perform(get("/create_user").with(user(standardUser))).andExpect(status().isForbidden());
		mockMvc.perform(post("/process_register").with(user(standardUser)).with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void adminCanRegisterUniqueUserWithoutDuplicateRoles() throws Exception {
		CustomUserDetails admin = buildPrincipal("admin", 1L);

		mockMvc.perform(post("/process_register").with(user(admin)).with(csrf())
				.param("firstName", "Alice")
				.param("lastName", "Smith")
				.param("email", "Alice@Example.com")
				.param("password", "StrongPass123"))
				.andExpect(status().isOk())
				.andExpect(view().name("register_success"));

		assertThat(userRepository.findByEmail("alice@example.com")).isNotNull();
		long roleCountAfterFirst = roleRepository.count();

		mockMvc.perform(post("/process_register").with(user(admin)).with(csrf())
				.param("firstName", "Bob")
				.param("lastName", "Jones")
				.param("email", "bob@example.com")
				.param("password", "AnotherPass123"))
				.andExpect(status().isOk())
				.andExpect(view().name("register_success"));

		// Role row for "user" is reused instead of creating duplicates
		assertThat(roleRepository.count()).isEqualTo(roleCountAfterFirst);

		// Duplicate email is rejected cleanly
		mockMvc.perform(post("/process_register").with(user(admin)).with(csrf())
				.param("firstName", "Dup")
				.param("lastName", "User")
				.param("email", "alice@example.com")
				.param("password", "AnotherPass123"))
				.andExpect(status().isOk())
				.andExpect(view().name("success"));
	}

	@Test
	public void performanceMetricsAreRecordedAndAdminOnly() throws Exception {
		CustomUserDetails standardUser = buildPrincipal("user", 2L);
		CustomUserDetails admin = buildPrincipal("admin", 1L);

		mockMvc.perform(get("/faq")).andExpect(status().isOk());
		mockMvc.perform(get("/docs")).andExpect(status().isOk());

		assertThat(pagePerformanceMetricRepo.count()).isGreaterThanOrEqualTo(2L);

		mockMvc.perform(get("/admin/performance").with(user(standardUser))).andExpect(status().isForbidden());
		mockMvc.perform(get("/admin/performance").with(user(admin)))
				.andExpect(status().isOk())
				.andExpect(view().name("performance"));
	}
}
