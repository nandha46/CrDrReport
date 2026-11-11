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
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.services.CSVService;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.util.CSVUtil;

@Controller
public class CSVController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVController.class);

	@Autowired
	private CSVService csvService;

	@Autowired
	private CompSelectionRepo compSelectionRepo;

	@GetMapping("/uploadcsv")
	public String uploadCSV(Model model) {
		model.addAttribute("pageTitle", "Upload CSV File");
		model.addAttribute("csvUploadModel", new CSVUploadModel());
		return "uploadCSV";
	}

	@PostMapping("/csvuploaded")
	public String success(@AuthenticationPrincipal CustomUserDetails user, Model model, CSVUploadModel csvUploadModel) {
		Long companyId = compSelectionRepo.findCompanyIdByUserId(user.getId());
		if (companyId == null) {
			companyId = 0L;
		}
		StringBuilder result = new StringBuilder();
		MultipartFile[] files = { csvUploadModel.getFile1(), csvUploadModel.getFile2(), csvUploadModel.getFile3(),
				csvUploadModel.getFile4() };

		boolean anyProcessed = false;
		for (MultipartFile file : files) {
			if (file == null || file.isEmpty()) {
				continue;
			}
			anyProcessed = true;
			if (!CSVUtil.hasCSVFormat(file)) {
				result.append("Unsupported file format (expected .csv): ")
						.append(file.getOriginalFilename()).append('\n');
				continue;
			}
			try {
				csvService.save(user.getId(), companyId, file);
				result.append("Uploaded the file successfully: ").append(file.getOriginalFilename()).append('\n');
			} catch (Exception e) {
				LOGGER.error("Failed to upload CSV file {}", file.getOriginalFilename(), e);
				result.append("Could not upload the file: ").append(file.getOriginalFilename())
						.append(" (").append(e.getMessage()).append(")\n");
			}
		}

		if (!anyProcessed) {
			result.append("Please select at least one CSV file to upload.");
		}
		String message = result.toString().trim();
		model.addAttribute("pageTitle", "CSV Upload Status");
		model.addAttribute("message", message);
		LOGGER.info("CSV upload summary for user {}: {}", user.getId(), message);
		return "success";
	}
}
