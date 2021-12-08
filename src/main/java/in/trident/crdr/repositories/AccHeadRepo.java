package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.AccHead;
@Repository
public interface AccHeadRepo extends JpaRepository<AccHead, Long> {
	
	@Query("Select a from AccHead a where a.userid = ?1 and a.companyid = ?2")
	public List<AccHead> findAllAccHead(Long uid, Long cid); 
	
	@Query("select accName from AccHead a where a.userid = ?1 and a.companyid = ?2")
	public List<String> findAccNames(Long uid, Long cid);
	
	@Query("select accCode from AccHead a where a.userid = ?1 and a.companyid = ?2 order by sNo")
	public List<Integer> findAccCodes(Long uid, Long cid);
	
	@Query("select level1 from AccHead a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public Integer findLevelByAccCode(Integer code, Long uid, Long cid);
	
	@Query("select accName from AccHead a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public String findAccNameByAccCode(Integer code, Long uid, Long cid);
	
	@Query("select shortName from AccHead a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public String findShortNameByAccHead(int accCode, Long uid, Long cid);
	
	@Query("select crAmt from AccHead a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public Double findCrAmt(Integer code, Long uid, Long cid);
	
	@Query("select drAmt from AccHead a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public Double findDrAmt(Integer code, Long uid, Long cid);
	
	@Query(value = "select datediff(?1,?2)", nativeQuery = true)
	public int findDaysBetween(String d1, String d2);
	
	@Query(value = "select a from AccHead a where a.accType = ?1 and a.userid = ?2 and a.companyid = ?3")
	public List<AccHead> findAccHeadByAccType(String accType, Long uid, Long cid);
	
	@Query(value = "select a from AccHead a where a.userid = ?1 and a.companyid = ?2 and a.orderCode between 3 and 6")
	public List<AccHead> findTradingPLAccs(Long uid, Long cid);
}
