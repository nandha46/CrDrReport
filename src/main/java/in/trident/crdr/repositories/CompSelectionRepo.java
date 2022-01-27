package in.trident.crdr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.CompanySelection;

@Repository
public interface CompSelectionRepo extends JpaRepository<CompanySelection, Long> {

	@Query("select c from CompanySelection c where c.companyId = ?1")
	public CompanySelection findCompanyByCompanyid(Long cid);

	@Query("select companyId from CompanySelection c where c.userId = ?1")
	public Long findCompanyIdByUserId(Long uid);

	@Query("select c from CompanySelection c where c.userId = ?1")
	public CompanySelection findCompanyByUser(Long uid);

	@Modifying
	@Query("delete CompanySelection c where c.userId = ?1")
	public void deleteAllByUserId(Long uid);

}
