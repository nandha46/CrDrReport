package in.trident.crdr.services;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	public void init();

	public Path save(MultipartFile file, Long uid, String checksum, String uname);

	public Resource load(String filename);

	public void delete(String filename);

	public void deleteAll();

}
