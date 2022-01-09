package in.trident.crdr.services;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.customExceptions.FileTypeException;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.util.DatabaseUtil;

@Service
public class MDBService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MDBService.class);

	@Autowired
	private FileStorageService fileStorage;

	@Autowired
	private DatabaseUtil dbutil;

	@Autowired
	private CompSelectionRepo csr;

	private static Connection conn = null;

	public void saveToStorage(MultipartFile file, Long uid, String username) throws SQLException, FileTypeException {
		fileStorage.init();
		Long cid = csr.findCompanyIdByUserId(uid);
		if (file == null) {
			LOGGER.error("file is null");
		}

		if (dbutil.hasMdbFormat(file)) {
			Path filename = fileStorage.save(file, uid, cid, username);
			conn = AccessConnectionService.getConnection("jdbc:ucanaccess://" + filename);
			dbutil.databaseDetails(conn);
			Statement s = conn.createStatement();

			dbutil.storeTableAccHead(s,uid,cid);
			/*
			 * access.readTableCloseBl(s); access.readTableCompany(s);
			 * access.readTableDayBook(s); access.readTableSchedule(s);
			 */

		} else {
			LOGGER.error("Incorrect file format");
			throw new FileTypeException("Incorrect File type uploaded");
		}

	}

}
