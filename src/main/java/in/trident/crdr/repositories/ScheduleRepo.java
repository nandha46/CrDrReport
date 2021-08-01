package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer>{

	@Query("select a from Schedule a")
	public List<Schedule> findAllAccounts();
	
	@Query("select drAmt from Schedule a where a.accCode = ?1")
	public Double findDrAmt(Integer code);
	
	@Query("select crAmt from Schedule a where a.accCode = ?1")
	public Double findCrAmt(Integer code);
	
}
