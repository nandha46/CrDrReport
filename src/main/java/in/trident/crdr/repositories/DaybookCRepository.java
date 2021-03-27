package in.trident.crdr.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookBalance;

public interface DaybookCRepository extends CrudRepository<Daybook, Long> {

//	@Query(value="select new in.trident.crdr.entities.DaybookBalance(date, sum(crAmt), sum(drAmt), sum(crAmt)-sum(drAmt), DAYNAME(?1) ) from Daybook u where u.date = ?1", nativeQuery = true)
//	public DaybookBalance findDaybookBalance(String d1);
	
	@Query("select date, sum(crAmt), sum(drAmt), sum(crAmt)-sum(drAmt), DAYNAME(?1) from Daybook u where u.date = ?1")
	public DaybookBalance findDaybookBalance(String d1);
}
