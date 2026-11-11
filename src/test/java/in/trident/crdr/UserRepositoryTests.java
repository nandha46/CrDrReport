package in.trident.crdr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.RoleRepository;
import in.trident.crdr.repositories.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private EntityManager entityManager;

	@Test
	public void testCreateUser() {
		Role role = new Role();
		role.setRoleName("user");
		role = roleRepo.save(role);

		User user = new User();
		user.setEmail("tester@example.com");
		user.setFirstName("Test");
		user.setLastName("User");
		user.setPassword("$2a$10$E.nXRSPx6vadp9wOrQwMLOOettOskYpORmoTA6e2pmwzqUdt2F6Fu");
		user.setEnabled(true);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		user.setRoles(roles);

		User savedUser = repo.save(user);
		entityManager.flush();
		User found = entityManager.find(User.class, savedUser.getId());

		assertThat(found).isNotNull();
		assertThat(found.getEmail()).isEqualTo("tester@example.com");
		assertThat(found.getRoles()).extracting(Role::getRoleName).containsExactly("user");
	}
}
