package name_service;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import mware_lib.Communication; 

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
 */
public class NameServiceDaemon implements Runnable 
{
    /**
     * Variable needed to set up the communication between the distributed 
     * software
     */
	private Communication nameServiceCom;
	/**
	 * Thread safe HashMap containing the elements of the Name Service
	 */
	private ConcurrentHashMap<String, String> nameServiceElements;
	
	private Logger logger;
	
	public NameServiceDaemon(Socket socket, ConcurrentHashMap<String, String> map) 
	{
		this.nameServiceElements = map;
		this.nameServiceCom = new Communication(socket);
		try {
			FileHandler hand = new FileHandler("NameServiceDaemon"+this.toString()+".log");
			logger = Logger.getLogger("NameServiceDaemon"+this.toString()+"_Logger");
			logger.addHandler(hand);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * Declares the remote object under the specified name
	 * to the name service. 
	 * 
	 * If the name already exists, the method will
	 * throw a RemoteException thrown
	 * 
	 * @param name - name of the declared object
	 * @param infos - info about the remote object
	 */
	public void rebind(String name, String infos) 
	{
		String infosTmp = nameServiceCom.getHostAddr() + "|" + infos;
		logger.info("[Rebind] Name: "+name +" Infos: "+infos);
		if (!nameServiceElements.containsKey(name)) 
		{
			nameServiceElements.put(name, infosTmp);
			
			if (!nameServiceElements.containsKey(name)) 
				throw new RuntimeException("Call of [" + name + "] failed...");
		} 
	}			
	
	/**
	 * Get's the object back under it's saved info. If the name
	 * is not mentioned in the object list, a RuntimeException will 
	 * be thrown
	 * 
	 * @param name - name of the known declared object
	 * @return String- info for the object
	 */
	public String resolve(String name) 
	{
		if (nameServiceElements.get(name) == null){
			logger.severe("ERROR: Passed name was null");
			throw new RuntimeException("ERROR: Passed name was null");
		}
		return nameServiceElements.get(name);
	}
	
	/**
	 * Unmarshalles a given marshalled string and calls the
	 * corresponding Method with its parameters if it has some.
	 * 
	 * If no exception occurres the "OK"-Code will be send back
	 * otherwise an "ERROR" Code
	 * 
	 * @param marshalled String that shall be marshalled
	 */
	private void unmarshall(String marshalled) 
	{
		String[] unmarshalled = marshalled.split("\\|\\|");
		String method = unmarshalled[0];
		
		if (method.equals("rebind"))
		{
			try {
				rebind(unmarshalled[2], unmarshalled[1] + "|" + unmarshalled[2]);
				nameServiceCom.send("OK|");
			} catch (RuntimeException e) {
				logger.severe("[Rebind] Error");
				nameServiceCom.send("ERROR|" + e.getMessage());
			}
		} else if (method.equals("resolve")) 
		{
			try {
				nameServiceCom.send("OK|" + resolve(unmarshalled[1]));
			} catch(RuntimeException e) {
				logger.severe("[Resolve] Error");
				nameServiceCom.send("ERROR|" + e.getMessage());
			}
		}else
		{
			logger.severe("This should be unreachable");
			System.err.println("err what the hecK? How did this happend? Looks like i took the wrong direction...");
		}
	}
	
	/**
	 * Waits on requests and calls after the request was receivd the unmarshall()-Method with the 
	 * marshalled String.
	 */
	@Override
	public void run() 
	{
		while (true)
		{
			unmarshall(nameServiceCom.receive());
		}
	}
}
