package mware_lib;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

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
	/**
	 * Constructor to create a new Skeleton, this constructor is 
	 * private because this class is implemented as singleton 
	 */
	private Skeleton() 
	{
		isRunning = true; 
		
		try 
		{
			serverSocket = new ServerSocket(PORT);
			logInfo("Skeleton started at port "+PORT);
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println("failure: create serverSocket");
			logError("failure: create serverSocket");
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
		logInfo("[addObject] Name: "+name);
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
			} catch (IOException e) {
				//System.out.println("ERROR @ start SkeletonDaemon");
				logError("[Run] ERROR @ start SkeletonDaemon");
			}			
		}
	}
	
	/**
	 * Stops the Skeleton-Instance 
	 */
	public void stop()
	{
		//System.out.println("Skeleton Thread-Instance will be stopped");
		logInfo("[Stop] Skeleton Thread-Instance will be stopped");
		isRunning = false;
	}
	
	private void logInfo(String log){
		LoggerImpl.info(this.getClass().getName(), log);
	}
		
	private void logError(String log){
		LoggerImpl.error(this.getClass().getName(), log);
	}
}
