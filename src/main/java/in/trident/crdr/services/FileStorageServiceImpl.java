package in.trident.crdr.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


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
			Files.createDirectories(root);
		} catch (IOException e) {
			LOGGER.error("Could not initialize folder for upload!", e);
		}
	}

	@Override
	public Path save(MultipartFile file, Long uid, String checksum, String uname) {
		String rawName = file.getOriginalFilename() == null ? "upload.mdb" : file.getOriginalFilename();
		String cleanName = Paths.get(StringUtils.cleanPath(rawName)).getFileName().toString();
		String safeUser = uname == null ? "user" : uname.replaceAll("[^a-zA-Z0-9._@-]", "_");
		Path targetDir = this.root.toAbsolutePath().normalize();
		Path filename = targetDir.resolve(uid + "_" + checksum + "_" + cleanName + "_" + safeUser + ".mdb").normalize();
		if (!filename.startsWith(targetDir)) {
			throw new IllegalArgumentException("Invalid upload filename: " + cleanName);
		}
		try {
			Files.copy(file.getInputStream(), filename, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Could not store the file", e);
			throw new RuntimeException("Could not store the file!", e);
		}
		LOGGER.info("Absolute Filename: {}", filename);
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
		}
		return null;
	}

	@Override
	public void delete(String filename) {
		Path file = root.resolve(filename);
		try {
			Files.delete(file);
		} catch (IOException e) {
			LOGGER.error("Error deleting the file", e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());
		LOGGER.warn("All Files in upload folder deleted");

	}

}
