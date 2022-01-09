package in.trident.crdr.customExceptions;

public class NoRecordException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9116728021354088990L;

	/**
	 * 
	 */

	public NoRecordException () {
		// Default constructor
	}
	
	public NoRecordException(String message, Throwable cause) {
		super(message, cause);
	}
}
