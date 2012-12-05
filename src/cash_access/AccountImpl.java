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
			accountCom = new Communication(new Socket(host, Integer.parseInt(port)));
			this.accountId = accountId;
		} catch (UnknownHostException e) {
			System.out.println("failure: AccountImplProxy unknown host");
		} catch (IOException e) {
			System.out.println("failure: io");
		}
	}
	
	@Override
	public synchronized final void withdraw(double amount) throws OverdraftException 
	{
		String marshalled = "withdraw|" + accountId + "|" + String.valueOf(amount) + "|" + new Double(amount).getClass();
		
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		//Check if the reply was successful
		if (reply[0].equals("ERROR")) 
		{
			if(reply[1].equals("class java.lang.RuntimeException"))
				throw new RuntimeException(reply[2]);
			
			else if(reply[1].equals("class cash_access.OverdraftException"))
				throw new OverdraftException(reply[2]);
			else
				throw new RuntimeException(reply[2]);
		}
			
	}

	@Override
	public final synchronized void deposit(double amount) 
	{
		String marshalled = "deposit|" + accountId + "|" + String.valueOf(amount) + "|" + new Double(amount).getClass();
		
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		//Check if the reply was successful
		if (reply[0].equals("ERROR")) 
		{
			if(reply[1].equals("class java.lang.RuntimeException"))
				throw new RuntimeException(reply[2]);
			
			else if(reply[1].equals("class cash_access.OverdraftException"))
				throw new RuntimeException("Catched the wrong exception" + reply[1] + " msg: " + reply[2]);
			else
				throw new RuntimeException(reply[2]);
		}
	}
	
	@Override
	public final synchronized double getBalance() 
	{
		String marshalled = "getBalance|" + accountId + "|" + null + "|" + null;
		
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
				
		//Check if the reply was successful
		if (reply[0].equals("ERROR"))
			throw new RuntimeException(reply[1] + "Error @ getBalance(), illegal operation performed");
				
		if(reply.length > 1)
			return Double.parseDouble(reply[1]);
		else 
			return 0.0;
	}
}