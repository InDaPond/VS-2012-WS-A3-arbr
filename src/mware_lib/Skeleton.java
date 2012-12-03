package mware_lib;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gr�bke
 */
public class Skeleton implements Runnable 
{

	/**
	 * Port to run the skeleton at
	 */
	public final static int PORT = 26000;
	/**
	 * Reference to the server socket
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Instance of the Skeleton
	 */
	private static Skeleton instance;
	/**
	 * Thread safe map that contains the list of all objects
	 */
	private ConcurrentHashMap<String, Object> objectList;
	/**
	 * Flag to signalize if the thread is running or not
	 */
	private boolean isRunning;
	
	private Logger logger;
	/**
	 * Constructor to create a new Skeleton, this constructor is 
	 * private because this class is implemented as singleton 
	 */
	private Skeleton() 
	{
		isRunning = true; 
		FileHandler hand;
		try {
			hand = new FileHandler("Skeleton.log");
			logger = Logger.getLogger("Skeleton_Logger");
			logger.addHandler(hand);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try 
		{
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println("failure: create serverSocket");
			logger.severe("failure: create serverSocket");
		}
		
		objectList = new ConcurrentHashMap<String, Object>();
		new Thread(this).start(); 
	}

	/**
	 * Creates only once a skeleton-objet and get it's reference back
	 * 
	 * @return Skeleton Reference to the one and only skeleton instance
	 */
	public static Skeleton getInstance() 
	{
		return (instance == null)? instance = new Skeleton():instance;
	}
	
	/**
	 * Helpmethod to add and save the objects with the list of the 
	 * Skeleton
	 */
	public void addObject(Object object, String name) 
	{
		objectList.put(name, object);
	}
	
	/**
	 * Waits for a connection request to run a skeleton as deamon at this
	 * reference. The skeleton gets a list of all known objects to the nameservice
	 */
	@Override
	public void run() 
	{
		while (isRunning) 
		{
			try 
			{
				Thread daemonThread = new Thread(new SkeletonDaemon(serverSocket.accept(), objectList)); 
				daemonThread.setDaemon(true);
				daemonThread.start();
				logger.info("SkeletonDaemon started");
			} catch (IOException e) {
				//System.out.println("ERROR @ start SkeletonDaemon");
				logger.severe("ERROR @ start SkeletonDaemon");
			}			
		}
	}
	
	/**
	 * Stops the Skeleton-Instance 
	 */
	public void stop()
	{
		//System.out.println("Skeleton Thread-Instance will be stopped");
		logger.info("Skeleton Thread-Instance will be stopped");
		isRunning = false;
	}
}
