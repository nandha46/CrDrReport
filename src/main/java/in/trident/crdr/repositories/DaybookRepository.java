package in.trident.crdr.repositories;

/**
 * @author Nandhakumar Subramanian
 * 
 * @version 0.1
 * @since day 1
 */
import java.util.ArrayList;

/**
 * @author Nandha
 */
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.Daybook;

@Repository
public interface DaybookRepository extends JpaRepository<Daybook, Long> {

	@Query("select u from Daybook u where u.date = ?1 and u.userid = ?2")
	public ArrayList<Daybook> findDaybookByDate(String date, Long userid);

	@Query("Select u from Daybook u where u.date between ?1 and ?2")
	public ArrayList<Daybook> findDaybookRange(String d1, String d2);

	@Query("Select u from Daybook u where u.acccode = ?1 and u.userid =?3 and u.date between ?2 and ?3 order by u.date asc")
	public List<Daybook> findDaybookByAccCodeAndDate(int accCode, String d1, String d2, Long userid);

	@Query("select date, sum(crAmt) as crTot, sum(drAmt) as drTot, sum(crAmt)-sum(drAmt) as dayBal, DAYNAME(?1) as dayOfWeek from Daybook u where u.date = ?1")
	public List<Object[]> findDaybookBalance(String d1);

	@Query(value = "select datediff(?1,?2)", nativeQuery = true)
	public int findDaysBetween(String d1, String d2);

	@Query(value = "select dayname(?1)", nativeQuery = true)
	public String findDayOfWeek(String date);

	@Query("select sum(crAmt)-sum(drAmt) as bal from Daybook d where d.acccode = ?1 and d.userid = ?4 and d.date between ?2 and ?3")
	public Double openBal(int acccode, String d1, String d2, Long userid);
}
