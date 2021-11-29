package in.trident.crdr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.CompanySelection;

public interface CompSelectionRepo extends JpaRepository<CompanySelection, Long> {

	@Query("select c from CompanySelection c where c.companyId = ?1")
	public CompanySelection findCompanyByCompanyid(Long cid);
	
}
