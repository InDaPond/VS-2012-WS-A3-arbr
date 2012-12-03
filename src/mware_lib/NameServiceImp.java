/**
 * 
 */
package mware_lib;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import branch_access.Manager;
import branch_access.ManagerImpl;
import cash_access.Account;
import cash_access.AccountImpl;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
 */
public class NameServiceImp extends NameService
{
    /**
     * Variable needed to set up the communication between the distributed 
     * software
     */
	private Communication nameServiceCom;
	
	private Logger logger;
	/**
	 * Constructor of the NameService Implementation 
	 * @param host IP Address of the host
	 * @param port Port of the host
	 */
    public NameServiceImp(String host, int port)
    {
		try {
			nameServiceCom = new Communication(new Socket(host, port));
			FileHandler hand = new FileHandler("NameServiceImp.log");
			logger = Logger.getLogger("NameServiceImp_Logger");
			logger.addHandler(hand);
			//System.out.println("connected to NameService");
			logger.info("connected to NameService at "+host+":"+port);
		} catch (UnknownHostException e) {
			//System.out.println("failure: nsProxy unknown host");
			logger.severe("failure: nsProxy unknown host");
		} catch (IOException e) {
			//System.out.println("failure: io");
			logger.severe("failure: io");
		}
    }
    
    @Override
    public void rebind(Object servant, String name)
    {
		int port = 0;
		String className = null;
		Skeleton skeleton = Skeleton.getInstance();
		port = Skeleton.PORT;
		String marshaled = null;
		String[] reply = null;
		
		if (servant instanceof Manager) {
			className = ManagerImpl.class.getCanonicalName();
		} else if (servant instanceof Account) {
			className = AccountImpl.class.getCanonicalName();
		} else {
			throw new RuntimeException("The passed Object "
					+ servant.toString() + "is invalid for this NameService");
		}
		
		//Marshal String and create replay
		marshaled = "rebind||" + port + "|" + className + "||"+ name;
		nameServiceCom.send(marshaled);
		logger.info("[Rebind] Port:"+port+" Classname:"+className);
		reply = nameServiceCom.receive().split("\\|");
		
		//Validate the reply
		if (reply[0].equals("OK")) {
			logger.info("Rebind was successful");
			skeleton.addObject(servant, name);
		}else {
			logger.severe("Rebind was NOT successful");
			throw new RuntimeException("BAM" + reply[1]);
		}

    }

    @Override
    public Object resolve(String name)
    {
    	List<String> paramList = new ArrayList<String>();
		Class<?>[] classParam = null;
		String[] reply = null;
		String marshaled = null;
		String className = null;
		String port = null;
		String hostAdress = null;
		
		marshaled = "resolve||" + name;
		logger.info("[Resolve] Name: "+name);
		nameServiceCom.send(marshaled);
		reply = nameServiceCom.receive().split("\\|");

		//Check if reply is free of errors
		if (reply[0].equals("ERROR")) {
			logger.info("Resolve was NOT successful");
			throw new RuntimeException(reply[1]);
		}

		className = reply[3];
		port = reply[2];
		hostAdress = reply[1];

		paramList.add(hostAdress);
		paramList.add(port);
		
		System.out.println("=================================");
		for(int i = 0; i < 4; i++)
		{
			System.out.println("reply[" + i + "] = " + reply[i]);
		}
		System.out.println("=================================");
		logger.info("[Resolve (reply)]= Status: "+reply[0]+" Hostadress: "+reply[1]+" Port: "+reply[2]+" Classname: "+reply[3]);
		try {
			for (Constructor<?> constructor : Class.forName(className).getConstructors()) 
				classParam = constructor.getParameterTypes();

			if (classParam.length > 2) 
				paramList.add(reply[4]);

			Arrays.toString(classParam);
			
			return Class.forName(className).getConstructor(classParam).newInstance(paramList.toArray());
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			logger.severe("ERROR @ NameServiceImp: Can't create Proxy");
			//System.out.println("ERROR @ NameServiceImp: Can't create Proxy");
		}
		logger.severe("This should be Unreachable");
		return "This return should be Unreachable...";
    }

}
