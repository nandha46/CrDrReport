package in.trident.crdr.models;

import org.springframework.web.multipart.MultipartFile;
/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.9c
 * 
 * @since 9 jan 2022
 *
 */
public class MDBUploadModel {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "MDBUploadModel [file=" + file + "]";
	}

}
