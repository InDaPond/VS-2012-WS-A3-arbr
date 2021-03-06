    package mware_lib;

import mware_lib.LoggerImpl;

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
    }

    /**
     * Function that gives an instance of the Object Broker back
     * @param serviceHost IP Address of the host
     * @param listenPort Port of the host
     * @return
     */
    public static ObjectBroker getBroker(String serviceHost, int listenPort)
    {
        // 'Ere we go again, oppan gangnam-brainfuck style
    	if(instance == null){
    		instance = new ObjectBroker(serviceHost, listenPort);
    		instance.logInfo("[getBroker] Host: "+serviceHost+" Port: "+listenPort);
    	}
    	return instance;
      //  return (instance == null) ? instance = new ObjectBroker(serviceHost, listenPort) : instance;
    }

    /**
     * Creates a NameService and get's it back
     * @return Freshly created NameService with the host IP + Port that are specified in this class
     */
    public NameService getNameService()
    {
        return new NameServiceImp(serviceHost, listenPort);
    }
    
    private void logInfo(String log){
		LoggerImpl.info(this.getClass().getName(), log);
	}
	
	@SuppressWarnings("unused")
	private void logError(String log){
		LoggerImpl.error(this.getClass().getName(), log);
	}
}
