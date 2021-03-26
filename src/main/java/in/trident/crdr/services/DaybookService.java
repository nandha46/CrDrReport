package in.trident.crdr.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.DaybookBalance;
import in.trident.crdr.entities.FormData;
import in.trident.crdr.repositories.DaybookRepository;

public class DaybookService implements DaybookRepository {

	@Autowired
	private DaybookRepository daybookRepo;
	
	@Autowired
	private FormData formdata;
	
	@Override
	public ArrayList<Daybook> findDaybookByDate(String date) {
		ArrayList<Daybook> daybook = daybookRepo.findDaybookByDate(date);
		return daybook;
	}
	
	@Override
	public ArrayList<Daybook> findDaybookByAccCodeAndDate(int accCode, String d1, String d2) {
		return daybookRepo.findDaybookByAccCodeAndDate(accCode, d1, d2);
	}
	
	@Override
	public ArrayList<Daybook> findDaybookRange(String d1, String d2) {
		int days = formdata.findDays(d1, d2);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		try {
			calender.setTime(df.parse(formdata.getStartDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ArrayList<Daybook> daybookList;
		ArrayList<ArrayList<Daybook>> listOflist = new ArrayList<ArrayList<Daybook>>();
		// TODO need clarification
		for (int i = 0; i <= days; i++) {
			daybookList = daybookRepo.findDaybookByDate(df.format(calender.getTime()));
			listOflist.add(daybookList);
			calender.add(Calendar.DATE, 1);
		}
		return null;
	}
	
	
	@Override
	public List<Daybook> findAll() {
		List<Daybook> daybooks = daybookRepo.findAll();
		return daybooks;
	}

	@Override
	public List<Daybook> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Daybook> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Daybook> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public <S extends Daybook> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<Daybook> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub

	}

	@Override
	public Daybook getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Daybook> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Daybook> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Daybook> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Daybook> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Daybook> findById(Long id) {
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
	public void delete(Daybook entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends Daybook> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
	}

	@Override
	public <S extends Daybook> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Daybook> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Daybook> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends Daybook> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DaybookBalance findDaybookBalance(String d1) {
		DaybookBalance db = daybookRepo.findDaybookBalance(d1);
		System.out.println(db);
		return null;
	}

	

}
