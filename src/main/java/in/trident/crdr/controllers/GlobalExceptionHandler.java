package in.trident.crdr.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler returning clean user-friendly error pages without leaking internals.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
		LOGGER.warn("Invalid request argument: {}", ex.getMessage());
		model.addAttribute("pageTitle", "Invalid Request");
		model.addAttribute("message", ex.getMessage());
		return "success";
	}

	@ExceptionHandler(Exception.class)
	public String handleUnexpectedException(Exception ex, Model model) {
		LOGGER.error("Unhandled application error", ex);
		model.addAttribute("pageTitle", "Unexpected Error");
		model.addAttribute("message", "An unexpected error occurred while processing your request.");
		return "error/500";
	}
}
