package in.trident.crdr.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.trident.crdr.entities.AccHead;
@Repository
public interface AccHeadRepo extends JpaRepository<AccHead, Long> {
	
	@Query("Select u from AccHead u")
	public ArrayList<AccHead> findAllAccHead(); 
	
	@Query("select shortName from AccHead a where accCode = ?1")
	public String findShortNameByAccHead(int accCode);
	
}
