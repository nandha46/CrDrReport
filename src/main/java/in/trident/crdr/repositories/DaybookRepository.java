package in.trident.crdr.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.Daybook;

public interface DaybookRepository extends JpaRepository<Daybook, Long> {
	
	@Query("Select u from Daybook u where u.date = ?1")
	public ArrayList<Daybook> findDaybookByDate(String date);
	
	//public ArrayList<Daybook> findDaybookRange(String d1, String d2);
	
}
