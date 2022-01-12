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

	private static Connection conn = null;

	public void saveToStorage(MultipartFile file, Long uid, String username) throws SQLException, FileTypeException {
		fileStorage.init();
		if (file == null) {
			LOGGER.error("file is null");
		}

		if (dbutil.hasMdbFormat(file)) {

			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				LOGGER.error("Error getting MD5 Instance", e);
			}
			String checksum = "";
			try {
				checksum = MD5Checksum.checksum(digest, file);
			} catch (IOException e) {
				LOGGER.error("Error generating checksum", e);
			}
			Path filename = fileStorage.save(file, uid, checksum, username);
			conn = AccessConnectionService.getConnection("jdbc:ucanaccess://" + filename);
			dbutil.databaseDetails(conn);
			Statement s = conn.createStatement();

			List<Company> companies = dbutil.storeTableCompany(s, uid);
			Long cid = companies.get(0).getCompanyid();

			dbutil.storeTableAccHead(s, uid, cid);
			dbutil.storeTableCloseBal(s, uid, cid);
			dbutil.storeTableDaybook(s, uid, cid);
			dbutil.storeTableSchedule(s, uid, cid);

		} else {
			LOGGER.error("Incorrect file format");
			throw new FileTypeException("Incorrect File type uploaded");
		}

	}

}
