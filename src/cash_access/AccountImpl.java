package cash_access;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import mware_lib.Communication;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
 */
public class AccountImpl extends Account {

	/**
	 * Contains the reference to the Communication class
	 */
	private Communication accountCom;
	
	/**
	 * String that represents the account ID
	 */
	private String accountId;
	
	/**
	 * Constructor to create a new Account
	 * @param host IP Adress of the host
	 * @param port Port of the host
	 * @param accountId ID of the count to do further operations
	 */
	public AccountImpl(String host, String port, String accountId) {
		try {
			System.out.println("AccountImpl Constructor");
			accountCom = new Communication(new Socket(host, Integer.parseInt(port)));
			this.accountId = accountId;
		} catch (UnknownHostException e) {
			System.out.println("failure: AccountImplProxy unknown host");
		} catch (IOException e) {
			System.out.println("failure: io");
		}
	}
	
	@Override
	synchronized public void withdraw(double amount) throws OverdraftException 
	{
		String marshalled = "withdraw|" + accountId + "|" + String.valueOf(amount) + "|" + new Double(amount).getClass();
		
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		System.out.println("REPLY LENGTH: "+ reply.length);
		
		System.out.println("Replay... : " + reply[1] + " || " + reply[2]);
		
		//Check if the reply was successful
		if ((reply.length > 1) && (reply[0].equals("ERROR"))) 
		{
			if(reply[1].equals("class cash_access.RuntimeException"))
				throw new RuntimeException(reply[2] + " a RuntimeException occured" );
			
			else if(reply[1].equals("class cash_access.OverdraftException"));
				throw new OverdraftException(reply[2] + "an OverdraftException occurred");
		}
		else
			System.out.println("FEEEEEEEEEEEEEEEEEEHHHHHHHHHHHHHHHHHHHHHLLLLLLLLLLLLLLLER");
			
	}

	@Override
	synchronized public void deposit(double amount) 
	{
		String marshalled = "deposit|" + accountId + "|" + String.valueOf(amount) + "|" + new Double(amount).getClass();
		
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		//Check if the reply was successful
		if (reply[0].equals("ERROR")) 
			throw new RuntimeException(reply[2] + "the passed amount [" + amount + "] is invalid" );
	}
	
	@Override
	synchronized public double getBalance() 
	{
		String marshalled = "getBalance|" + accountId + "|" + null + "|" + null;
		
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		System.out.println("REPLY LENGTH: "+ reply.length);
		//Check if the reply was successful
		if (reply[0].equals("ERROR"))
			throw new RuntimeException(reply[1] + "Error @ getBalance(), illegal operation performed");
		
		if(reply.length > 1)
			return Double.parseDouble(reply[1]);
		else 
			return 0.0;
	}
}