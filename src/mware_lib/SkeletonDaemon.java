package mware_lib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import cash_access.OverdraftException;

/**
 * @author Benjamin Trapp Christoph Gr�bke
 */
public class SkeletonDaemon implements Runnable {

	/**
	 * Variable needed to set up the communication between the distributed
	 * software
	 */
	private Communication skeletonDaemonCom;
	/**
	 * ConcurrentHashMap to represent the object list
	 */
	private ConcurrentHashMap<String, Object> objectList;
	/**
	 * Set on true, to see further debug info (Reply String)
	 */
	private boolean DEBUG = false; 

	/**
	 * Constructor of the skeleton daemon
	 * 
	 * @param socket
	 *            socket that is needed for the communication
	 * @param objectList
	 *            lists from the nameservice with all known objects
	 */
	public SkeletonDaemon(Socket socket, ConcurrentHashMap<String, Object> objectList) {
		this.objectList = objectList;
		this.skeletonDaemonCom = new Communication(socket);
	}
	
	/**
	 * This method prints a complete stack trace from a passed exception
	 * @param e occurred exception
	 * @return String containing the complete stack trace of the passed exception
	 */
	private String stacktraceToString(Exception e)
	{
		StringWriter errors = new StringWriter();
		e.getCause().printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	/**
	 * Waits for a request call from the calling class and splits the request in
	 * its components to receive the methodname, the name of the declared
	 * object, the parameter and the type of the parameter.
	 * 
	 * After the check, the object will be put from the object list to perform a
	 * getMethod() call and execute it via invoke() and its parameters if it has
	 * one or more.
	 * 
	 * Finally the result of the methodcall will be send back
	 */
	@Override
	public void run() {
		logInfo("SkeletonDaemon started");
		while (true) {
			Object returnVal = null;
			Object object = null;

			String receive = skeletonDaemonCom.receive();
			String[] unmarshalled = receive.split("\\|");

			// Match all necessary info for further processing
			String methodName = unmarshalled[0];
			String name = unmarshalled[1];
			String param = unmarshalled[2];
			String paramTyp = unmarshalled[3];
			Class<?> parameterTypes[] = { String.class };
			logInfo("Methodname: "+methodName+" Param: "+param+" Name: "+name);
			if(DEBUG == true)
			{
				System.out.println("-----------------------------");
				for (int i = 0; i < unmarshalled.length; i++) {
					System.out.println("unmarshalled (SkeletonDaemon) [" + i
							+ "] = " + unmarshalled[i]);
				}
				System.out.println("-----------------------------");
			}

			// Check if the name is mentioned in the object list
			if (objectList.containsKey(name)) {
				object = objectList.get(name);

				/*
				 * Determine the parameter types for the getMethod() call (The
				 * types null, double and String are currently only accepted)
				 */
				if (paramTyp.equals("null")) {
					logInfo("paramType is null");
					parameterTypes = null;
				} else if (paramTyp.equals(Double.class.toString())) {
					logInfo("paramType is Double");
					parameterTypes[0] = double.class;
				} else if (paramTyp.equals(String.class.toString())) {
					logInfo("paramType is String");
					parameterTypes[0] = String.class;
				}

				try {

					Method method = object.getClass().getMethod(methodName,
							parameterTypes);

					// Check if method is Accessible
					// (http://stackoverflow.com/questions/5184284/illegalaccessexception-on-using-reflection)
					if (!method.isAccessible())
						method.setAccessible(true);

					/*
					 * After the correct parameter type was determined the
					 * invoke()-Method can be called
					 */
					if (parameterTypes == null) {
						returnVal = method.invoke(object, (Object[]) null);
					} else if (parameterTypes[0].toString().equals(
							double.class.toString())) {
						returnVal = method.invoke(object,
								Double.parseDouble(param));
					} else {
						returnVal = method.invoke(object, param);
					}

					/*
					 * If the return type of the called method is void, there is
					 * no need to do a String interpretation appended on the
					 * OK-Code
					 */
					if (!method.getReturnType().equals(Void.TYPE))
						skeletonDaemonCom.send("OK|" + returnVal.toString());
					else
						skeletonDaemonCom.send("OK|");

				} catch (SecurityException e) {
					logError("SecurityException");
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					logError("NoSuchMethodException");
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					logError("IllegalArgumentException");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					logError("IllegalAccessException");
					e.printStackTrace();
				} catch (Exception e) { // Catch the Exception that may be thrown by a real method-invoke call
					String exceptiontype = null;
				
					if(e instanceof InvocationTargetException)
					{
						if(e.getCause() instanceof RuntimeException)
							exceptiontype = e.getCause().getClass().toString();
						else if(e.getCause() instanceof OverdraftException)
							exceptiontype = e.getCause().getClass().toString();
						else
							exceptiontype = e.getCause().getClass().toString();
					}
					
					String tmp = stacktraceToString(e);
					System.out.println(tmp);	//Prints the stack trace at the bank output... 
					logInfo("Caught "+exceptiontype+" and informed about it");
					skeletonDaemonCom.send("ERROR|" + exceptiontype + "|" + e.getCause().getMessage() + "|" + tmp);
				}
			} else {
				logError("Invalid key in object list");
				skeletonDaemonCom.send("ERROR|" + "Invalid key in object list");
			}
		}
	}
	
	private void logInfo(String log){
		LoggerImpl.info(this.toString(), log);
	}
		
	private void logError(String log){
		LoggerImpl.error(this.toString(), log);
	}

}