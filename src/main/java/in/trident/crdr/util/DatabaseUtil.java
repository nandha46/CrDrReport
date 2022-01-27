package in.trident.crdr.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

	/**
	 * Shows detail about list of Tables
	 * 
	 * @param Connection c
	 * @throws SQLException
	 */
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

	/**
	 * Shows Details about a table's Column and datatype
	 * 
	 * @param ResultSet rs
	 * @throws SQLException
	 */
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
	/**
	 * This method takes Java.sql.Date object and returns LocalDateTime
	 * 
	 * @param Java.sql.Date
	 * @return LocalDateTime
	 */
	private static LocalDateTime convert(Date date) {
		if (date == null)
			return null;
		return new Timestamp(date.getTime()).toLocalDateTime();
	}

	public List<Company> storeTableCompany(Statement s, Long uid) throws SQLException {
		String sql = "select * from Company";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===Company===");
		List<Company> companies = new LinkedList<>();

		while (rs.next()) {

			LOGGER.info(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getBoolean(4) + " "
					+ rs.getDate(5) + " " + rs.getDate(6) + " " + rs.getString(7) + " " + rs.getString(8) + " "
					+ rs.getString(9) + " " + rs.getString(10) + " " + rs.getString(11) + " " + rs.getString(12) + " "
					+ rs.getString(13) + " " + rs.getString(14) + " " + rs.getString(15) + " " + rs.getBoolean(16) + " "
					+ rs.getString(17) + " " + rs.getString(18) + " " + rs.getBoolean(19) + " " + rs.getDate(20) + " "
					+ rs.getDate(21) + " " + rs.getInt(22) + " " + rs.getInt(23) + " " + rs.getDate(24) + " "
					+ rs.getString(25) + " " + rs.getString(26) + " " + rs.getString(27) + " " + rs.getString(28) + " "
					+ rs.getDate(29) + " " + rs.getString(30) + " " + rs.getString(31) + " " + rs.getInt(32) + " "
					+ rs.getInt(33) + " " + rs.getDate(34) + " " + rs.getString(35) + " " + rs.getString(36) + " "
					+ rs.getDate(37) + " " + rs.getDate(38) + " " + rs.getDate(39) + " " + rs.getDate(40) + " "
					+ rs.getDouble(41) + " " + rs.getDouble(42) + " " + rs.getDouble(43) + " " + rs.getDouble(44) + " "
					+ rs.getBoolean(45) + " " + rs.getString(46) + " " + rs.getBoolean(47) + " " + rs.getString(48)
					+ " " + rs.getString(49));

			companies.add(new Company(uid, rs.getString(1), rs.getString(2), rs.getString(3), rs.getBoolean(4),
					convert(rs.getDate(5)), convert(rs.getDate(6)), rs.getString(7), rs.getString(8), rs.getString(9),
					rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
					rs.getString(15), rs.getBoolean(16), rs.getString(17), rs.getString(18), rs.getBoolean(19),
					convert(rs.getDate(20)), convert(rs.getDate(21)), rs.getInt(22), rs.getInt(23),
					convert(rs.getDate(24)), rs.getString(25), rs.getString(26), rs.getString(27), rs.getString(28),
					convert(rs.getDate(29)), rs.getString(30), rs.getString(31), rs.getInt(32), rs.getInt(33),
					convert(rs.getDate(34)), rs.getString(35), rs.getString(36), convert(rs.getDate(37)),
					convert(rs.getDate(38)), convert(rs.getDate(39)), convert(rs.getDate(40)), rs.getDouble(41),
					rs.getDouble(42), rs.getDouble(43), rs.getDouble(44), rs.getBoolean(45), rs.getString(46),
					rs.getBoolean(47), rs.getString(48), rs.getString(49)));

		}
		LOGGER.info(companies.toString());
		return companyRepo.saveAll(companies);
	}

	public void storeTableAccHead(Statement s, Long uid, Long cid) throws SQLException {
		String sql = "select * from AccHead";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===AccHead===");
		List<AccHead> accHeads = new LinkedList<>();
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
		String sql = "select * from CloseBl";
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

	public void storeTableSchedule(Statement s, Long uid, Long cid) throws SQLException {
		String sql = "select * from Schedule";
		ResultSet rs = s.executeQuery(sql);
		// Display table details
		showTableDetails(rs);
		LOGGER.info("===Schedule===");
		List<Schedule> schedules = new LinkedList<>();

		while (rs.next()) {
			schedules.add(new Schedule(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5),
					rs.getInt(7), rs.getInt(8), uid, cid));

		}
		scheduleRepo.saveAll(schedules);
		LOGGER.info(schedules.toString());
	}

}
