package in.trident.crdr.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.DayBal;

public interface DaybookBalRepository extends JpaRepository<DayBal, Long> {

	@Query("select new in.trident.crdr.entities.DayBal(date, sum(crAmt), sum(drAmt), sum(crAmt)-sum(drAmt), DAYNAME(?1)) from Daybook u where u.date = ?1")
	public DayBal findDaybookBalance(String d1);
	
}
