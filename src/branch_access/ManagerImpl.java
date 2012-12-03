/**
 * 
 */
package branch_access;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

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
	
	private Logger logger;
	/**
	 * Constructor of the Manager class
	 * @param serviceHost IP Address of the Host
	 * @param listenPort Port to listen for communication 
	 */
	public ManagerImpl(String host, String port, String bankName)
	{
		this.bankName = bankName;
		FileHandler hand;
		try {
			hand = new FileHandler("ManagerImpl.log");
			logger = Logger.getLogger("ManagerImp_Logger");
			logger.addHandler(hand);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			managerCom = new Communication(new Socket(host, Integer.parseInt(port)));
		} catch (IOException e) {
			logger.severe("IO Exception");
			e.printStackTrace();
		} 
	}
	
    /* (non-Javadoc)
     * @see aufgabe3.branch_access.Manager#createAccount(java.lang.String)
     */
    @Override
    public String createAccount(String owner)
    {
		String marshalled = "createAccount|" + bankName + "|" +  owner + "|" +  owner.toString();
		logger.info("[createAccount] Bank:"+ bankName+" Owner: "+owner);
		managerCom.send(marshalled);
		String[] reply = managerCom.receive().split("\\|");
		
		//Validate the reply 
		if (reply[0].equals("OK")) 
		{
			logger.info("[createAccount] reply was successful");
			return reply[1];
		} else {
			logger.severe("[createAccount] reply was NOT successful");
			throw new RuntimeException("ERROR during creation of the Account");
		}
    }

    /* (non-Javadoc)
     * @see aufgabe3.branch_access.Manager#removeAccount(java.lang.String)
     */
    @Override
    public double getBalance(String accountID)
    {
		String marshalled = "getBalance|" + bankName + "|" + accountID + "|" + accountID.toString();
		logger.info("[getBalance] Bank: "+bankName+" accountID: "+accountID);
		managerCom.send(marshalled);
		String[] reply = managerCom.receive().split("\\|");
		
		//Validate the reply 
		if (reply[0].equals("OK")) 
		{
			logger.info("[getBalance] reply was successful");
			return Double.parseDouble(reply[1]);
		} else {
			//throw new RuntimeException("ERROR @ getBalance (ManagerImp)");
		    //System.out.println("ERROR @ getBalance (ManagerImp), Illegal Operation on accountID" + accountID);
			logger.severe("ERROR @ getBalance (ManagerImp), Illegal Operation on accountID" + accountID);
		    return new Double(null);
		}
    }

}
