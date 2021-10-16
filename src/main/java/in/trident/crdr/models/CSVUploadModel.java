package in.trident.crdr.models;

import org.springframework.web.multipart.MultipartFile;
/**
 * 
 * @author Nandhakumar Subramanian
 *
 * @version 0.0.6c
 * 
 * @since 15 Oct 2021
 */
public class CSVUploadModel {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
}
