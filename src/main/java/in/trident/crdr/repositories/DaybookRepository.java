package in.trident.crdr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.Daybook;

public interface DaybookRepository extends JpaRepository<Daybook, Long> {
	
	@Query("Select u from Daybook u where u.date = ?1")
	public Daybook findDaybookByDate();
	
	//TODO Daybook search method filter by from and to dates
}
