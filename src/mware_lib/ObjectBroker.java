package mware_lib;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gr�bke
 */
public class ObjectBroker
{
	/**
	 * Instance of the object broker
	 */
	private static ObjectBroker instance;
	/**
	 * IP of the Host which contains the name service that shall be contacted
	 */
	private String serviceHost;
	/**
	 * Port of the Host which contains the name service that shall be contacted
	 */
	private int listenPort;
	
	private Logger logger;
	
	/**
	 * Implemented as singleton, because there should be only one instance
	 * of the ObjectBroker.
	 * 
	 * Original-Description:
	 *    // Das hier zur�ckgelieferte Objekt soll der zentrale Einstiegspunkt 
     *    // der Middleware aus Anwendersicht sein.
     *    // Parameter: Host und Port, bei dem die Dienste (Namensdienst)
     *    //            kontaktiert werden sollen.
	 * 
	 * 
	 * @param serviceHost
	 * @param listenPort
	 */
    private ObjectBroker(String serviceHost, int listenPort)
    {
        this.serviceHost = serviceHost;
        this.listenPort = listenPort;
        FileHandler hand;
		try {
			hand = new FileHandler("ObjectBroker.log");
			logger = Logger.getLogger("ObjectBroker_Logger");
			logger.addHandler(hand);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Function that gives an instance of the Object Broker back
     * @param serviceHost IP Address of the host
     * @param listenPort Port of the host
     * @return
     */
    public static ObjectBroker getBroker(String serviceHost, int listenPort)
    {
    	//System.out.println("@get Instance ObjectBroker");
    	if(instance == null){
    		instance = new ObjectBroker(serviceHost, listenPort);
    	}
    	instance.logger.info("@get Instance ObjectBroker");
        return instance;
    }

    /**
     * Creates a NameService and get's it back
     * @return Freshly created NameService with the host IP + Port that are specified in this class
     */
    public NameService getNameService()
    {
        return new NameServiceImp(serviceHost, listenPort);
    }
}
