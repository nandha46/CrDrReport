package in.trident.crdr.customExceptions;

public class FileTypeException extends Exception {

	/**
	 * @author Nandhakumar Subramanian
	 * 
	 */
	private static final long serialVersionUID = 9200083700155664197L;
	
	public FileTypeException() {
		// Default
	}
	
	public FileTypeException (String message, Throwable cause) {
		super(message,cause);
	}

	public FileTypeException (String message) {
		super(message);
	}
	
}
