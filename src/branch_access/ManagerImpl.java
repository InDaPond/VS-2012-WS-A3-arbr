/**
 * 
 */
package branch_access;

import java.io.IOException;
import java.net.Socket;

import mware_lib.Communication;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
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
		managerCom.send(marshalled);
		String[] reply = managerCom.receive().split("\\|");
		
		//Validate the reply 
		if (reply[0].equals("OK")) 
		{
			return reply[1];
		} else {
			throw new RuntimeException(reply[2]);
		}
    }


    @Override
    public final synchronized double getBalance(String accountID)
    {
		String marshalled = "getBalance|" + bankName + "|" + accountID + "|" + accountID.toString();
		managerCom.send(marshalled);
		String[] reply = managerCom.receive().split("\\|");
		
		//Validate the reply 
		if (reply[0].equals("OK")) 
		{
			return Double.parseDouble(reply[1]);
		} else {
			throw new RuntimeException(reply[2]);
		}
    }

}
