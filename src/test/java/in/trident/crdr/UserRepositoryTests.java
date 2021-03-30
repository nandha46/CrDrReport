package in.trident.crdr;


/**
 * @author Nandhakumar Subramanian
 */
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.test.annotation.Rollback;

import in.trident.crdr.entities.DayBal;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookBalance;
import in.trident.crdr.entities.User;
import in.trident.crdr.repositories.DaybookBalRepository;
import in.trident.crdr.repositories.DaybookCRepository;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
    private TestEntityManager entityManager;
     
    @Autowired
    private UserRepository repo;

    @Autowired
    private DaybookRepository daybookRepo;

    @Autowired
    private DaybookBalRepository dbalrepo;
    
    /*
    @Test
    public void testCreateUser() {
    	User user = new User();
    	user.setEmail("nandha@icloud.com");
    	user.setFirstName("Nandhakumar");
    	user.setLastName("Subramanian");
    	user.setPassword("pass@1");

    	User savedUser = repo.save(user);
    	User user2 = entityManager.find(User.class, savedUser.getId());
    	
    	assertThat(user.getEmail().equals(user2.getEmail()));
 
    } */
    
    @Test
    public void testDaybookBal() {
    	DayBal dbbal = dbalrepo.findDaybookBalance("2020-04-15");
    	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<");
    	System.out.println(dbbal);
    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
    	
    }
}