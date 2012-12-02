package cash_access;

public class OverdraftException extends Exception {
    /**
     * This variable is stupid but necessary....
     */
	private static final long serialVersionUID = 1L;
	
	/**
	 * OverdraftExcaption, this exception can occurre if the balance 
	 * of an Account is Overdrafted
	 * @param message message for further debug details
	 */
	public OverdraftException(String message) 
	{
		super(message);
	}
}