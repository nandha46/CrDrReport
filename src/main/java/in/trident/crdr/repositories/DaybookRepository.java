package in.trident.crdr.repositories;
/**
 * @author Nandhakumar Subramanian
 */
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.Daybook;
@Repository
public interface DaybookRepository extends JpaRepository<Daybook, Long> {
	
	@Query("select u from Daybook u where u.date = ?1")
	public ArrayList<Daybook> findDaybookByDate(String date);
	
	@Query("Select u from Daybook u where u.date between ?1 and ?2")
	public ArrayList<Daybook> findDaybookRange(String d1, String d2);
	
	@Query("Select u from Daybook u where u.acccode = ?1 and u.date between ?2 and ?3")
	public ArrayList<Daybook> findDaybookByAccCodeAndDate(int accCode, String d1, String d2);
	
	@Query("select date, sum(crAmt) as crTot, sum(drAmt) as drTot, sum(crAmt)-sum(drAmt) as dayBal, DAYNAME(?1) as dayOfWeek from Daybook u where u.date = ?1")
	public List<Object[]> findDaybookBalance(String d1);
	
	@Query(value = "select datediff(?1,?2)", nativeQuery = true)
	public int findDaysBetween(String d1, String d2);
	
	@Query(value="select dayname(?1)", nativeQuery = true)
	public String findDayOfWeek(String date);

}
