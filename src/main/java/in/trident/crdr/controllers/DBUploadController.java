package in.trident.crdr.controllers;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.customExceptions.FileTypeException;
import in.trident.crdr.services.CustomUserDetails;
import in.trident.crdr.services.DatabaseService;

/**
 * 
 * @author Nandhakumar Subramainan
 * 
 * @since 9 jan 2022
 * 
 * @version 0.0.9c
 *
 */
@Controller
public class DBUploadController {

	@Autowired
	private DatabaseService mdbService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DBUploadController.class);

	@GetMapping("/upload")
	public String uploadMDB(Model model) {
		model.addAttribute("pageTitle", "Upload Access Databse");
		return "upload_db";
	}

	@PostMapping("/uploaded")
	public String uploadSuccess(@AuthenticationPrincipal CustomUserDetails user, Model model,
			@RequestParam(value = "file") MultipartFile uploadedFile) {
		try {
			mdbService.saveToStorage(uploadedFile, user.getId(), user.getUsername());
		} catch (SQLException e) {
			LOGGER.error("SQL Error", e);
			model.addAttribute("message", "SQL Error: Contact Admin");
			model.addAttribute("pageTitle", "SQL Error");
			return "success";
		} catch (FileTypeException e) {
			LOGGER.error(e.getMessage());
			model.addAttribute("message", "Incorrect Filetype!. Please Check the file type.");
			model.addAttribute("pageTitle", "Error");
			return "success";
		}
		LOGGER.info("Uploaded the file successfully: {} \n", uploadedFile.getOriginalFilename());
		model.addAttribute("message", "File uploaded successfully! ");
		model.addAttribute("pageTitle", "Success! ");
		return "success";
	}

}
