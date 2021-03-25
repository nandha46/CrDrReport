package in.trident.crdr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.FormData;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;

/**
 * @author Nandhakumar Subramanian
 * 
 */

public class AccHeadService implements AccHeadRepo {

	@Autowired
	private AccHeadRepo accHeadRepo;
	
	@Autowired
	private DaybookRepository daybookRepo;
	
	
	@Override
	public ArrayList<ArrayList<Daybook>> showLedger(FormData formdata) {
		ArrayList<Daybook> daybooklist = new ArrayList<>();
		ArrayList<ArrayList<Daybook>> listOflist = new ArrayList<ArrayList<Daybook>>();
		if(formdata.isReportOrder()) { // true -> All acc heads
			ArrayList<AccHead> headlist = accHeadRepo.findAllAccHead();
			for (AccHead acchead : headlist) {
				if (acchead.getAccCode() == 0) {
					// Intentionally left empty
				}
				else {
					daybooklist = daybookRepo.findDaybookByAccCodeAndDate(acchead.getAccCode(), formdata.getStartDate(), formdata.getEndDate());
				}
				listOflist.add(daybooklist);
			}
			
		}
		else {
			
		}
		return listOflist;
	} 
	
	
	
	@Override
	public ArrayList<AccHead> findAll() {
		ArrayList<AccHead> accHeadList = accHeadRepo.findAllAccHead();
		return accHeadList;
	}

	@Override
	public List<AccHead> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccHead> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AccHead> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public <S extends AccHead> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<AccHead> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub

	}

	@Override
	public AccHead getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AccHead> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AccHead> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AccHead> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AccHead> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AccHead> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(AccHead entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends AccHead> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public <S extends AccHead> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AccHead> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AccHead> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends AccHead> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<AccHead> findAllAccHead() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
