package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer>{

	@Query("select a from Schedule a where a.userid = ?1 and a.companyid = ?2 order by sNo")
	public List<Schedule> findAllAccounts(Long uid, Long cid);
	
	@Query("select a from Schedule a where a.userid = ?1 and a.companyid = ?2 and a.orderCode = 1 order by sNo")
	public List<Schedule> findAllAssetAccounts(Long uid, Long cid);
	
	@Query("select a from Schedule a where a.userid = ?1 and a.companyid = ?2 and a.orderCode = 2 order by sNo")
	public List<Schedule> findAllLiabilityAccounts(Long uid, Long cid);
	
	@Query("select drAmt from Schedule a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public Double findDrAmt(Integer code, Long uid, Long cid);
	
	@Query("select crAmt from Schedule a where a.accCode = ?1 and a.userid = ?2 and a.companyid = ?3")
	public Double findCrAmt(Integer code, Long uid, Long cid);
	
}
