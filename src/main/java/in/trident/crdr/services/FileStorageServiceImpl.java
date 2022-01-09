package in.trident.crdr.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.repositories.CompSelectionRepo;

/**
 * 
 * @author Nandhakumar Subramainan
 * 
 * @since 9 jan 2022
 * 
 * @version 0.0.9c
 *
 */

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceImpl.class);
	private final Path root = Paths.get("uploads");

	@Override
	public void init() {
		try {
			Files.createDirectory(root);
		} catch (FileAlreadyExistsException e) {
			LOGGER.info("Folder Already exists");
		} catch (IOException e) {
			LOGGER.error("Could not initialize folder for upload!", e);
		}
	}

	@Override
	public Path save(MultipartFile file, Long uid, Long cid, String uname) {

		Path filename = this.root
				.resolve(uid.toString() + "_" + cid + "_" + file.getOriginalFilename() + "_" + uname + ".mdb");
		try {
			Files.copy(file.getInputStream(), filename, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Could not store the file", e);
			throw new RuntimeException("Could not store the file!");
		}
		LOGGER.info("Absolute Filename: " + filename);
		return filename;
	}

	@Override
	public Resource load(String filename) {
		Path file = root.resolve(filename);
		try {
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				LOGGER.error("Could not read the file");
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Malformed URL", e);
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void delete(String filename) {
		Path file = root.resolve(filename);
		try {
			Files.delete(file);
		} catch (IOException e) {
			LOGGER.error("Error deleting the file", e);
			throw new RuntimeException("Error deleting the file" + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());
		LOGGER.warn("All Files in upload folder deleted");

	}

}
