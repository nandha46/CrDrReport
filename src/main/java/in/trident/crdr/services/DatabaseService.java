package in.trident.crdr.services;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.customExceptions.FileTypeException;
import in.trident.crdr.entities.Company;
import in.trident.crdr.security.MD5Checksum;
import in.trident.crdr.util.DatabaseUtil;

@Service
public class DatabaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

	@Autowired
	private FileStorageService fileStorage;

	@Autowired
	private DatabaseUtil dbutil;

	public void saveToStorage(MultipartFile file, Long uid, String username) throws SQLException, FileTypeException {
		fileStorage.init();
		if (file == null || file.isEmpty()) {
			throw new FileTypeException("Please select an Access database (.mdb) file to upload.");
		}

		if (!dbutil.hasMdbFormat(file)) {
			LOGGER.error("Incorrect file format uploaded: {}", file.getOriginalFilename());
			throw new FileTypeException("Incorrect File type uploaded");
		}

		String checksum = computeMd5Checksum(file);
		Path filename = fileStorage.save(file, uid, checksum, username);
		try (Connection conn = AccessConnectionService.getConnection("jdbc:ucanaccess://" + filename);
				Statement s = conn.createStatement()) {
			dbutil.databaseDetails(conn);
			List<Company> companies = dbutil.storeTableCompany(s, uid);
			if (companies == null || companies.isEmpty()) {
				throw new SQLException("No company metadata found in uploaded Access database.");
			}
			Long cid = companies.get(0).getCompanyid();
			dbutil.storeTableAccHead(s, uid, cid);
			dbutil.storeTableCloseBal(s, uid, cid);
			dbutil.storeTableDaybook(s, uid, cid);
			dbutil.storeTableSchedule(s, uid, cid);
		}
	}

	private String computeMd5Checksum(MultipartFile file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return MD5Checksum.checksum(digest, file);
		} catch (NoSuchAlgorithmException | IOException e) {
			LOGGER.error("Error computing MD5 checksum", e);
			return "";
		}
	}
}
