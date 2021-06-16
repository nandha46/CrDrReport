package in.trident.crdr;


/**
 * @author Nandhakumar Subramanian
 */
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import in.trident.crdr.entities.Role;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private EntityManager entityManager;
    
    public void testCreateUser() {
    	User user = new User();
    	user.setEmail("asff@icloud.com");
    	user.setFirstName("test");
    	user.setLastName("user");
    	user.setPassword("pass@1");
    	Set<Role> roles = new HashSet<Role>();
    	Role role = new Role();
    	role.setRoleName("Tester");
		roles.add(role);
		user.setRoles(roles);
    	User savedUser = repo.save(user);
    	User user2 = entityManager.find(User.class, savedUser.getId());
    	
    	assertThat(user.getEmail().equals(user2.getEmail()));
 
    } 
    
}