package in.trident.crdr.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.AccHead;

public interface AccHeadRepo extends JpaRepository<AccHead, Long> {
	
	@Query("Select u from AccHead u")
	public ArrayList<AccHead> findAllAccHead(); 
	
	
}
