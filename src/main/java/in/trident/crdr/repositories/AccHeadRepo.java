package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.AccHead;
@Repository
public interface AccHeadRepo extends JpaRepository<AccHead, Long> {
	
	@Query("Select a from AccHead a")
	public List<AccHead> findAllAccHead(); 
	
	@Query("select accName from AccHead a")
	public List<String> findAccNames();
	
	@Query("select shortName from AccHead a where accCode = ?1")
	public String findShortNameByAccHead(int accCode);
	
	@Query(value = "select datediff(?1,?2)", nativeQuery = true)
	public int findDaysBetween(String d1, String d2);
}
