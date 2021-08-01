package in.trident.crdr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer>{

	@Query("select a from Schedule a")
	public List<Schedule> findAllAccounts();
	
	
}
