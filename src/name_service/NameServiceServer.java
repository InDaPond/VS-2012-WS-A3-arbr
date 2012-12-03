package name_service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
 * 
 */
public class NameServiceServer implements Runnable 
{
	/**
	 * Default port of the name service server
	 */
	private static int STD_PORT = 12000;
	
	/**
	 * Server socket for the communication 
	 */
	private ServerSocket serverSocket;
	/**
	 * Thread safe has map to store the name service elements  
	 */
	private ConcurrentHashMap<String, String> nameServiceElements;
	
	private Logger logger;
	
	/**
	 * DefaultConstrutor to create a new name service server on the default 
	 * port 12000
	 */
	public NameServiceServer() 
	{
		this(STD_PORT);
	}
	
	/**
	 * Use this constructor to create a new name service server
	 * operating on a port specified by the user
	 * @param port Port to run the server at
	 */
	public NameServiceServer(int port)
	{
		nameServiceElements = new ConcurrentHashMap<String, String>();
		try {
			FileHandler hand = new FileHandler("NameServiceServer.log");
			logger = Logger.getLogger("NameServiceServer_Logger");
			logger.addHandler(hand);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			serverSocket = new ServerSocket(port);
			//System.out.println("NameServiceServer started");
			logger.info("NameServiceServer started");
		} catch (IOException e) {
			//System.out.println("failure: new ServerSocket(port)");
			logger.severe("failure: new ServerSocket(port)");
		}
	}
	
	/**
	 * Waits on a connection request to run a NameService daemon.
	 * The daemon gets a list that contains all declared objects of the
	 *  name service
	 */
	private void waitForConnection() 
	{
		Thread tmp = null;
		
		//Create and mark the created Thread as Daemon
		try {
			tmp = new Thread(new NameServiceDaemon(serverSocket.accept(), nameServiceElements));
			tmp.setDaemon(true); 
			tmp.start();
			logger.info("ServerDaemon gestartet");
		} catch (IOException e) {
			//System.out.println("failure: NameService accept()");
			logger.severe("failure: NameService accept()");
		}
	}
	
	/**
	 * Infinite loop of the name service server to wait for
	 * a connection request. If there is a connection request 
	 * the new created daemons will do all further operations.
	 */
	@Override
	public void run() 
	{
		while (true) 
		{
			waitForConnection();
		}
	}

	/**
	 * Main method to run the name service 
	 * @param args Used to specify a port to run the name service at
	 */
	public static void main(String[] args) 
	{
		if (args.length == 1) {
			new Thread(new NameServiceServer(Integer.parseInt(args[0]))).start();
		} else {
			new Thread(new NameServiceServer()).start();
		}
	}
}
