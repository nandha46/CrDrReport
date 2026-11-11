package in.trident.crdr.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.repositories.DaybookRepository;

public class JasperFactory {

	private static DaybookRepository daybookRepo;

	public static void setDaybookRepo(DaybookRepository repo) {
		daybookRepo = repo;
	}

	public static Collection<Daybook> generateCollection() {
		return generateCollection(0L, 0L);
	}

	public static Collection<Daybook> generateCollection(Long uid, Long cid) {
		if (daybookRepo == null) {
			return Collections.emptyList();
		}
		List<Daybook> daybooks = daybookRepo.findDaybookRange("2018-04-01", "2019-03-31", uid, cid);
		return new Vector<>(daybooks);
	}
}
