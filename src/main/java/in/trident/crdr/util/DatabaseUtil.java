package in.trident.crdr.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.entities.CloseBal;
import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.Daybook;
import in.trident.crdr.entities.Schedule;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.CloseBalRepo;
import in.trident.crdr.repositories.CompanyRepo;
import in.trident.crdr.repositories.DaybookRepository;
import in.trident.crdr.repositories.ScheduleRepo;

@Service
public class DatabaseUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);

	private static final String FILE_TYPE = "application/octet-stream";

	@Autowired
	private DaybookRepository daybookRepo;
	@Autowired
	private AccHeadRepo accHeadrepo;
	@Autowired
	private CloseBalRepo closeBalrepo;
	@Autowired
	private CompanyRepo companyRepo;
	@Autowired
	private ScheduleRepo scheduleRepo;

	public Boolean hasMdbFormat(MultipartFile file) {
		LOGGER.info("Filetype: " + file.getContentType());
		if (FILE_TYPE.equals(file.getContentType())) {
			return true;
		} else {
			return false;
		}
	}

	public void databaseDetails(Connection c) throws SQLException {
		DatabaseMetaData metaData = c.getMetaData();
		// getTableTypes(metaData);
		String[] types = { "GLOBAL TEMPORARY", "SYSTEM TABLE", "TABLE", "VIEW" };
		ResultSet tables = metaData.getTables(null, null, "%", types);
		LOGGER.info("---------Table List------");
		while (tables.next()) {
			LOGGER.info("Table name: " + tables.getString("TABLE_NAME"));
		}
		LOGGER.info("------------------------");
	}

	public void showTableDetails(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmdata = rs.getMetaData();
		LOGGER.info("Table Name: " + rsmdata.getTableName(1));

		int count = rsmdata.getColumnCount();
		LOGGER.info("---Column List---");
		for (int i = 1; i <= count; i++) {
			LOGGER.info("Column " + i + ": " + rsmdata.getColumnLabel(i) + " \"" + rsmdata.getColumnTypeName(i) + "\"");
		}
		LOGGER.info("------------------");
	}

	public void storeTableAccHead(Statement s, Long uid, Long cid) throws SQLException {
		String sql = "select * from AccHead";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===AccHead===");
		List<AccHead> accHeads = new LinkedList<>();
		// TODO verify rs record order and constructor insert order
		while (rs.next()) {
			accHeads.add(new AccHead(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5),
					rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getString(9), rs.getDouble(10), rs.getDouble(11),
					rs.getBoolean(12), rs.getBoolean(13), rs.getDouble(14), rs.getString(15), uid, cid));

		}
		accHeadrepo.saveAll(accHeads);
		LOGGER.info(accHeads.toString());
	}

	public void storeTableDaybook(Statement s, Long uid, Long cid) throws SQLException {
		String sql = "select * from Daybook";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===Daybook===");
		List<Daybook> daybooks = new LinkedList<>();

		while (rs.next()) {
			daybooks.add(new Daybook(rs.getInt(1), rs.getDate(2).toString(), rs.getString(3), rs.getInt(4),
					rs.getDouble(5), rs.getDouble(6), rs.getInt(7), uid, cid));

		}
		daybookRepo.saveAll(daybooks);
		LOGGER.info(daybooks.toString());
	}

	public void storeTableCloseBal(Statement s, Long uid, Long cid) throws SQLException {
		String sql = "select * from CloseBal";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===CloseBal===");
		List<CloseBal> closeBals = new LinkedList<>();

		while (rs.next()) {
			closeBals.add(new CloseBal(rs.getDate(1).toString(), rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), uid,
					cid));

		}
		closeBalrepo.saveAll(closeBals);
		LOGGER.info(closeBals.toString());
	}

	public List<Company> storeTableCompany(Statement s, Long uid) throws SQLException {
		String sql = "select * from Company";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===Company===");
		List<Company> companies = new LinkedList<>();

		LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("Asia/Kolkata"));

		while (rs.next()) {
			companies.add(new Company(uid, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(47),
					rs.getString(49), rs.getString(4), rs.getBoolean(5),
					LocalDateTime.ofInstant(rs.getDate(6).toInstant(), ZoneId.of("Asia/Kolkata")),
					LocalDateTime.ofInstant(rs.getDate(7).toInstant(), ZoneId.of("Asia/Kolkata")), rs.getString(8),
					rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13),
					rs.getString(14), rs.getString(15), rs.getString(16), rs.getBoolean(17), rs.getString(18),
					rs.getString(19), rs.getBoolean(20),
					LocalDateTime.ofInstant(rs.getDate(21).toInstant(), ZoneId.of("Asia/Kolkata")),
					LocalDateTime.ofInstant(rs.getDate(22).toInstant(), ZoneId.of("Asia/Kolkata")), rs.getInt(23),
					rs.getInt(24), LocalDateTime.ofInstant(rs.getDate(25).toInstant(), ZoneId.of("Asia/Kolkata")),
					rs.getString(26), rs.getString(27), rs.getString(28), rs.getString(29),
					LocalDateTime.ofInstant(rs.getDate(30).toInstant(), ZoneId.of("Asia/Kolkata")), rs.getString(31),
					rs.getString(32), rs.getInt(33), rs.getInt(34),
					LocalDateTime.ofInstant(rs.getDate(35).toInstant(), ZoneId.of("Asia/Kolkata")), rs.getString(36),
					rs.getString(37), LocalDateTime.ofInstant(rs.getDate(38).toInstant(), ZoneId.of("Asia/Kolkata")),
					LocalDateTime.ofInstant(rs.getDate(39).toInstant(), ZoneId.of("Asia/Kolkata")), rs.getDate(40),
					LocalDateTime.ofInstant(rs.getDate(41).toInstant(), ZoneId.of("Asia/Kolkata")), rs.getDouble(42),
					rs.getDouble(43), rs.getDouble(44), rs.getDouble(45), rs.getBoolean(46), rs.getBoolean(48)));

		}
		LOGGER.info(companies.toString());
		return companyRepo.saveAll(companies);
	}

	public void storeTableSchedule(Statement s, Long uid, Long cid) throws SQLException {
		String sql = "select * from Schedule";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===Schedule===");
		List<Schedule> schedules = new LinkedList<>();

		while (rs.next()) {
			schedules.add(new Schedule(rs.getInt(1), rs.getString(2), rs.getDouble(4), rs.getDouble(5), rs.getInt(7),
					rs.getInt(8), rs.getString(3), uid, cid));

		}
		scheduleRepo.saveAll(schedules);
		LOGGER.info(schedules.toString());
	}

}
