package in.trident.crdr.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.Daybook;

public interface DaybookRepository extends JpaRepository<Daybook, Long> {
	
	@Query("Select u from Daybook u where u.date = ?1")
	public ArrayList<Daybook> findDaybookByDate(String date);
	
	@Query("Select u from Daybook u where u.date between ?1 and ?2")
	public ArrayList<Daybook> findDaybookRange(String d1, String d2);
	
	@Query("Select u from Daybook u where u.acccode = ?1 and u.date between ?2 and ?3")
	public ArrayList<Daybook> findDaybookByAccCodeAndDate(int accCode, String d1, String d2);

	
}
