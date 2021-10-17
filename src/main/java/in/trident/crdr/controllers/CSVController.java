package in.trident.crdr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.models.CSVUploadModel;
import in.trident.crdr.services.CSVService;
import in.trident.crdr.util.CSVUtil;

@Controller
public class CSVController {

	@Autowired
	private CSVService csvService;
	
	@GetMapping("/upload")
	public String uploadCSV(Model model) {
		model.addAttribute("pageTitle", "Upload CSV File");
		model.addAttribute("csvUploadModel", new CSVUploadModel());
		return "upload";
	}
	
	@PostMapping("/uploaded")
	public String success(Model model, CSVUploadModel csvUploadModel) {
		String message ="";
		MultipartFile file1 = csvUploadModel.getFile1();
		if (CSVUtil.hasCSVFormat(file1)) {
			try {
				csvService.save(file1);
		        message = "Uploaded the file successfully: " + file1.getOriginalFilename()+"/n";
			} catch (Exception e) {
				message = "Could not upload the file: "+file1.getOriginalFilename()+"/n";
			}
		}
		
		MultipartFile file2 = csvUploadModel.getFile1();
		if (CSVUtil.hasCSVFormat(file2)) {
			try {
				csvService.save(file2);
		        message.concat("Uploaded the file successfully: " + file2.getOriginalFilename()+"/n");
			} catch (Exception e) {
				message.concat("Could not upload the file: "+file2.getOriginalFilename()+"/n");
			}
		}
		MultipartFile file3 = csvUploadModel.getFile1();
		if (CSVUtil.hasCSVFormat(file3)) {
			try {
				csvService.save(file3);
		        message.concat("Uploaded the file successfully: " + file3.getOriginalFilename()+"/n");
			} catch (Exception e) {
				message.concat("Could not upload the file: "+file3.getOriginalFilename()+"/n");
			}
		}
		MultipartFile file4 = csvUploadModel.getFile1();
		if (CSVUtil.hasCSVFormat(file4)) {
			try {
				csvService.save(file4);
		        message.concat("Uploaded the file successfully: " + file4.getOriginalFilename()+"/n");
			} catch (Exception e) {
				message = "Could not upload the file: "+file4.getOriginalFilename()+"/n";
			}
		}
		model.addAttribute("message",message);
		
		return "success";
	}
}
