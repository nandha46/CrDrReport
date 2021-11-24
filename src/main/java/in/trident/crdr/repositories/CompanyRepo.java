package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.Company;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {

//	@Query("select * from Company c where c.userid = ?1")
//	public List<Company> findCompaniesByUser(Long userid);
	
	public List<Company> findAll();
}
