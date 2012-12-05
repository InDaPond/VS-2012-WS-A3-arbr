/**
 * 
 */
package mware_lib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Constructor;

import branch_access.*;
import cash_access.*;

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
	/**
	 * Set on true, to see further debug info (Reply String)
	 */
	private boolean DEBUG = false;
	
	/**
	 * Constructor of the NameService Implementation 
	 * @param host IP Address of the host
	 * @param port Port of the host
	 */
    public NameServiceImp(String host, int port)
    {
		try {
			nameServiceCom = new Communication(new Socket(host, port));
		} catch (UnknownHostException e) {
			System.out.println("failure: nsProxy unknown host");
		} catch (IOException e) {
			System.out.println("failure: io");
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
		
		//Marshal String and create reply
		marshaled = "rebind||" + port + "|" + className + "||"+ name;
		nameServiceCom.send(marshaled);
		reply = nameServiceCom.receive().split("\\|");
		
		//Validate the reply
		if (reply[0].equals("OK")) {
			skeleton.addObject(servant, name);
		}else {
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
		nameServiceCom.send(marshaled);
		reply = nameServiceCom.receive().split("\\|");

		//Check if reply is free of errors
		if (reply[0].equals("ERROR")) {
			throw new RuntimeException(reply[1]);
		}

		className = reply[3];
		port = reply[2];
		hostAdress = reply[1];

		paramList.add(hostAdress);
		paramList.add(port);
		
		if(DEBUG == true)
		{
			System.out.println("=================================");
			for(int i = 0; i < 4; i++)
			{
				System.out.println("reply[" + i + "] = " + reply[i]);
			}
			System.out.println("=================================");
		}
		
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
			System.out.println("ERROR @ NameServiceImp: Can't create Proxy");
		}
		
		return "This return should be Unreachable...";
    }

}
