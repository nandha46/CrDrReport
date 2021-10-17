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

	private MultipartFile file1,file2,file3,file4;

	public MultipartFile getFile1() {
		return file1;
	}

	public void setFile1(MultipartFile file1) {
		this.file1 = file1;
	}

	public MultipartFile getFile2() {
		return file2;
	}

	public void setFile2(MultipartFile file2) {
		this.file2 = file2;
	}

	public MultipartFile getFile3() {
		return file3;
	}

	public void setFile3(MultipartFile file3) {
		this.file3 = file3;
	}

	public MultipartFile getFile4() {
		return file4;
	}

	public void setFile4(MultipartFile file4) {
		this.file4 = file4;
	}

	@Override
	public String toString() {
		return "CSVUploadModel [file1=" + file1 + ", file2=" + file2 + ", file3=" + file3 + ", file4=" + file4 + "]";
	}

		
	
}
