package cash_access;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
	
	private Logger logger;
	
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
			FileHandler hand = new FileHandler("Account_"+accountId+".log");
			logger = Logger.getLogger("Account_"+accountId+"_Logger");
			logger.addHandler(hand);
		} catch (UnknownHostException e) {
			//System.out.println("failure: AccountImplProxy unknown host");
			logger.severe("failure: AccountImplProxy unknown host");
		} catch (IOException e) {
			//System.out.println("failure: io");
			logger.severe("failure: io");
		}
	}
	
	@Override
	public void withdraw(double amount) throws OverdraftException 
	{
		String marshalled = "withdraw|" + accountId + "|" + String.valueOf(amount) + "|" + new Double(amount).getClass();
		logger.info("[Withdraw] Account: "+accountId+" Amount: "+amount);
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		//Check if the reply was successful
		if (reply[0].equals("ERROR")){
			logger.severe("[Withdraw] reply was NOT succesful: + "+reply[1]);
			throw new OverdraftException(reply[1]);
		}
	}

	@Override
	public void deposit(double amount) 
	{
		String marshalled = "deposit|" + accountId + "|" + String.valueOf(amount) + "|" + new Double(amount).getClass();
		logger.info("[Deposit] Account: "+accountId+" Amount: "+amount);
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		//Check if the reply was successful
		if (reply[0].equals("ERROR")){
			logger.severe("[Deposit] reply was NOT successful: "+reply[1]);
			throw new RuntimeException(reply[1]);
		}
	}
	
	@Override
	public double getBalance() 
	{
		String marshalled = "getBalance|" + accountId + "|" + null + "|" + null;
		logger.info("[getBalance] Account: "+accountId);
		accountCom.send(marshalled);
		String[] reply = accountCom.receive().split("\\|");
		
		//Check if the reply was successful
		if (reply[0].equals("ERROR")){
			logger.severe("[getBalance] reply was NOT succesfull: "+reply[1]);
			throw new RuntimeException(reply[1]);
		}
		
		return Double.parseDouble(reply[1]);
	}
}