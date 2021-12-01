package in.trident.crdr.repositories;


import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.trident.crdr.entities.CloseBal;

public interface CloseBalRepo extends JpaRepository<CloseBal, Long> {

	@Query("select closeBal from CloseBal c where c.date= ?1 and c.userid = ?2 and c.companyid = ?3")
	public Double findCloseBalByDate(String date, Long uid, Long cid);
	
	@Query("select c from CloseBal c where c.userid = ?3 and c.companyid = ?4 and c.date between ?1 and ?2")
	public ArrayList<CloseBal> findCloseBalList(String d1, String d2, Long uid, Long cid);
	
	@Query("select crTot from CloseBal c where c.date = ?1 and c.userid = ?2 and c.companyid = ?3")
	public Double findCreditTotal(String date, Long uid, Long cid);
	
	@Query("select drTot from CloseBal c where c.date = ?1 and c.userid = ?2 and c.companyid = ?3")
	public Double findDebitTotal(String date, Long uid, Long cid);
}
