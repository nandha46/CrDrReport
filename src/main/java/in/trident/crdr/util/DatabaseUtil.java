package in.trident.crdr.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.repositories.DaybookRepository;

@Service
public class DatabaseUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);

	private static final String FILE_TYPE = "application/octet-stream";

	@Autowired
	private DaybookRepository daybookRepo;

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

		while (rs.next()) {
			accHeads.add(new AccHead(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getDouble(5),
					rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getString(9), rs.getDouble(10), rs.getDouble(11),
					rs.getBoolean(12), rs.getBoolean(13), rs.getDouble(14), rs.getString(15), uid, cid));

		}
		LOGGER.info(accHeads.toString());
	}

}
