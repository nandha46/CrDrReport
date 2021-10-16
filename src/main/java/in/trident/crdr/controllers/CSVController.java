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
		MultipartFile file = csvUploadModel.getFile();
		if (CSVUtil.hasCSVFormat(file)) {
			try {
				csvService.save(file);
		        message = "Uploaded the file successfully: " + file.getOriginalFilename();
			} catch (Exception e) {
				message = "Could not upload the file: "+file.getOriginalFilename();
			}
		}
		
		model.addAttribute("message",message);
		
		return "success";
	}
}
