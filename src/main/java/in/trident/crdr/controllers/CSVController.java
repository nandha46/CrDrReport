package in.trident.crdr.controllers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.models.CSVUploadModel;
import in.trident.crdr.services.CSVService;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.util.CSVUtil;

@Controller
public class CSVController {

	@Autowired
	private CSVService csvService;
	
	private static final Logger LOGGER =LoggerFactory.getLogger(CSVController.class);
	
	@GetMapping("/upload")
	public String uploadCSV(Model model) {
		model.addAttribute("pageTitle", "Upload CSV File");
		model.addAttribute("csvUploadModel", new CSVUploadModel());
		return "uploadCSV";
	}
	
	
	@PostMapping("/uploaded")
	public String success(@AuthenticationPrincipal CustomUserDetails user, Model model, CSVUploadModel csvUploadModel) {
		StringBuffer sbr = new StringBuffer();
		MultipartFile file1 = csvUploadModel.getFile1();
		if (CSVUtil.hasCSVFormat(file1)) {
			try {
				csvService.save(user.getId(), file1);
				sbr.append("Uploaded the file successfully: " + file1.getOriginalFilename()+"\n");
			} catch (Exception e) {
				sbr.append("Could not upload the file: "+file1.getOriginalFilename()+"\n");
			}
		}
		LOGGER.info(sbr.toString());
		MultipartFile file2 = csvUploadModel.getFile2();
		if (CSVUtil.hasCSVFormat(file2)) {
			try {
				csvService.save(user.getId(),file2);
				sbr.append("Uploaded the file successfully: " + file2.getOriginalFilename()+"\n");
			} catch (Exception e) {
				sbr.append("Could not upload the file: "+file2.getOriginalFilename()+"\n");
			}
		}
		LOGGER.info(sbr.toString());
		MultipartFile file3 = csvUploadModel.getFile3();
		if (CSVUtil.hasCSVFormat(file3)) {
			try {
				csvService.save(user.getId(),file3);
				sbr.append("Uploaded the file successfully: " + file3.getOriginalFilename()+"\n");
			} catch (Exception e) {
				sbr.append("Could not upload the file: "+file3.getOriginalFilename()+"\n");
			}
		}
		LOGGER.info(sbr.toString());
		MultipartFile file4 = csvUploadModel.getFile4();
		if (CSVUtil.hasCSVFormat(file4)) {
			try {
				csvService.save(user.getId(),file4);
		        sbr.append("Uploaded the file successfully: " + file4.getOriginalFilename()+"\n");
			} catch (Exception e) {
				sbr.append("Could not upload the file: "+file4.getOriginalFilename()+"\n");
			}
		}
		model.addAttribute("message",sbr.toString());
		LOGGER.info(sbr.toString());
		return "success";
	}
	
}
