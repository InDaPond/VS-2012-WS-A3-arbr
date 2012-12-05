/**
 * 
 */
package branch_access;

import java.io.IOException;
import java.net.Socket;

import mware_lib.Communication;
import mware_lib.LoggerImpl;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gr�bke
 */
public class ManagerImpl extends Manager
{
    /**
     * Variable needed to set up the communication between the distributed 
     * software
     */
	private Communication managerCom;
	/**
	 */
	private String bankName;
	
	/**
	 * Constructor of the Manager class
	 * @param serviceHost IP Address of the Host
	 * @param listenPort Port to listen for communication 
	 */
	public ManagerImpl(String host, String port, String bankName)
	{
		this.bankName = bankName; 
		try {
			managerCom = new Communication(new Socket(host, Integer.parseInt(port)));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
    @Override
    public final synchronized String createAccount(String owner)
    {
		String marshalled = "createAccount|" + bankName + "|" +  owner + "|" +  owner.toString();
		logInfo("[createAccount] bankName: "+bankName+" Owner: "+owner);
		managerCom.send(marshalled);
		String[] reply = managerCom.receive().split("\\|");
		
		//Validate the reply 
		if (reply[0].equals("OK")) 
		{
			logInfo("[createAccount] successful");
			return reply[1];
		} else {
			logError("[createAccount] NOT Successful");
			throw new RuntimeException(reply[2]);
		}
    }


    @Override
    public final synchronized double getBalance(String accountID)
    {
		String marshalled = "getBalance|" + bankName + "|" + accountID + "|" + accountID.toString();
		logInfo("[getBalance] bankName: "+bankName+" AccountID: "+accountID);
		managerCom.send(marshalled);
		String[] reply = managerCom.receive().split("\\|");
		
		//Validate the reply 
		if (reply[0].equals("OK")) 
		{
			logInfo("[getBalance] OK");
			return Double.parseDouble(reply[1]);
		} else {
			logInfo("[getBalance] ERROR");
			throw new RuntimeException(reply[2]);
		}
    }
    
	private void logInfo(String log){
		LoggerImpl.info(this.getClass().getName()+" "+bankName, log);
	}
		
	private void logError(String log){
		LoggerImpl.error(this.getClass().getName()+" "+bankName, log);
	}

}
