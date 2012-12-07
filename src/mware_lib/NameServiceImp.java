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

/**
 * @author Benjamin Trapp
 * 		   Christoph Gr�bke
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
			//System.out.println("failure: nsProxy unknown host");
			logError("failure: nsProxy unknown host");
		} catch (IOException e) {
			//System.out.println("failure: io");
			logError("failure: io");
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
		
		className = Object.class.getCanonicalName();
		Class<?> klass = servant.getClass();
		while (klass.getSuperclass() != null) {
			className = klass.getCanonicalName();
			klass = klass.getSuperclass();
		}
		
		//Marshal String and create reply
		marshaled = "rebind||" + port + "|" + className + "||"+ name;
		logInfo("[Rebind] Port: "+port+" className: "+className+" Name: "+name);
		nameServiceCom.send(marshaled);
		reply = nameServiceCom.receive().split("\\|");
		
		//Validate the reply
		if (reply[0].equals("OK")) {
			logInfo("[Rebind] seccessful");
			skeleton.addObject(servant, name);
		}else {
			logInfo("[Rebind] NOT successful");
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
		logInfo("[Resolve] Name: "+name);
		nameServiceCom.send(marshaled);
		reply = nameServiceCom.receive().split("\\|");

		//Check if reply is free of errors
		if (reply[0].equals("ERROR")) {
			logError("[Resolve] was NOT successful");
			throw new RuntimeException(reply[1]);
		}

		className = reply[3];
		port = reply[2];
		hostAdress = reply[1];
		logInfo("[Resolve] successful. className: "+className+" Port: "+port+" Host: "+hostAdress);
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
			// Find class for className (should be Middleware related)
			Class<?> klass = Class.forName(className+"Impl");
			
			// Find Constructor: String host, String port, String bankName
			Class<?>[] parameterTypes = { String.class, String.class, String.class }; 
			Constructor<?> constructor = klass.getDeclaredConstructor(parameterTypes);
			
			return constructor.newInstance(paramList.toArray());	
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("ERROR @ NameServiceImp: Can't create Proxy");
			logError("NameServiceImp: Can't create Proxy");
		}
		
		return "This return should be Unreachable...";
    }
    
    private void logInfo(String log){
		LoggerImpl.info(this.toString(), log);
	}
	
	private void logError(String log){
		LoggerImpl.error(this.toString(), log);
	}

}
