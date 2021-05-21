package in.trident.crdr.repositories;


import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.CloseBal;

public interface CloseBalRepo extends JpaRepository<CloseBal, Long> {

	@Query("select closeBal from CloseBal c where c.date= ?1")
	public Double findCloseBalByDate(String date);
	
	@Query("select u from CloseBal u where u.date between ?1 and ?2")
	public ArrayList<CloseBal> findCloseBalList(String d1, String d2);
	
	//@Query("select dayname(?1)")
	//public String findDayOfWeek(String date);
	
	@Query("select datediff(?1, ?2)")
	public int findDateDiff(String d1, String d2);
}
