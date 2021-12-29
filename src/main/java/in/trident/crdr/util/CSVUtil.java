/**
 * 
 */
package in.trident.crdr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.entities.Daybook;
import in.trident.crdr.services.CustomUserDetails;

/**
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.6c
 * 
 * @since 14 oct 2021
 *
 */
public class CSVUtil {
	public static String csv = "text/csv";
	public static String excel = "text/excel";
	static String[] headers = {};
	private static final Logger LOGGER =LoggerFactory.getLogger(CSVUtil.class);
	
	public static boolean hasCSVFormat(MultipartFile file) {
		LOGGER.info("File type: "+file.getContentType());
		if(csv.equals(file.getContentType())) {
			return true;
		}
		return false;
	}
	
	public static boolean hasExcelFormat(MultipartFile file) {
		LOGGER.info("File type: "+file.getContentType());
		if(excel.equals(file.getContentType())) {
			return true;
		}
		return false;
	}
	
	public static List<Daybook> csvToDaybook(Long userid, InputStream in){
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader, 
						CSVFormat.DEFAULT.builder().setTrim(true).setSkipHeaderRecord(true).setIgnoreHeaderCase(true).build());
				){
			List<Daybook> daybooks = new ArrayList<>();
			Iterable<CSVRecord> records = csvParser.getRecords();
			for (CSVRecord record : records) {
				Daybook daybook = new Daybook(
						Integer.parseInt(record.get("Sno")),
						record.get("TDate"),
						record.get("Narration"),
						Integer.parseInt(record.get("acccode")),
						Double.parseDouble(record.get("dramt")),
						Double.parseDouble(record.get("cramt")),
						Integer.parseInt(record.get("stkvalue")),
						userid
						);
				daybooks.add(daybook);
			}
			return daybooks;
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV file: "+ e.getMessage());
		}
	}
	
	public static List<Daybook> ExcelToDaybook(@AuthenticationPrincipal CustomUserDetails user,InputStream in){
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader, 
						CSVFormat.DEFAULT.builder().setTrim(true).setSkipHeaderRecord(true).setIgnoreHeaderCase(true).build());
				){
			List<Daybook> daybooks = new ArrayList<>();
			Iterable<CSVRecord> records = csvParser.getRecords();
			for (CSVRecord record : records) {
				Daybook daybook = new Daybook(
						Integer.parseInt(record.get("Sno")),
						record.get("TDate"),
						record.get("Narration"),
						Integer.parseInt(record.get("acccode")),
						Double.parseDouble(record.get("dramt")),
						Double.parseDouble(record.get("cramt")),
						Integer.parseInt(record.get("stkvalue")),
						user.getId()
						);
				daybooks.add(daybook);
			}
			return daybooks;
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV file: "+ e.getMessage());
		}
	}
	
	public void processAccessDatabse() {
		Connection conn;
		try {
		//	Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		 conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Trident/Demo/CrDr/Data/18-19.001/18-19.001","","shuttle");
		 Statement s = conn.createStatement();
		 String sql = "select * from DayBook";
		 ResultSet rs = s.executeQuery(sql);
		 while (rs.next()) {
			LOGGER.info(rs.getString(3)+"\n");
		 }
		} catch (SQLException e) {
			LOGGER.error("SQL Error in mdb file",e);
		} 
		//catch (ClassNotFoundException e) {
	//		LOGGER.info("Class not found" + e);
		//}
		
	}
}

