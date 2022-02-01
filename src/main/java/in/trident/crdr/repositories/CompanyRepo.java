package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.Company;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {

	@Query("select c from Company c where c.userid = ?1 order by compName")
	public List<Company> findCompaniesByUser(Long uid);
	
	@Query(value = "select DISTINCT c.compName from Company c where c.userid = ?1", nativeQuery = true)
	public List<String> findUniqueCompanyByUser(Long uid);
	
	@Query("select c from Company c where c.compName = ?1 and c.userid = ?2")
	public List<Company> findCompanyYearsByName(String cname, Long uid);
	
	@Query("select c from Company c where c.companyid = ?1")
	public Company findCompanyById(Long cid);

	@Modifying
	@Query("delete Company c where c.userid = ?1")
	public void deleteAllByUserId(Long uid); 
	
}
