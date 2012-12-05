/**
 * 
 */
package mware_lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.management.InvalidApplicationException;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
 */
public abstract class ACom
{
    /**
     * Input Stream
     */
    protected BufferedReader inStream = null;
    
    /**
     * Output Stream
     */
    protected OutputStream outStream = null;
    
    /**
     * Constructor of the Communication class
     * @param socket Socket to handle the communication
     */
	public ACom(Socket socket) 
	{
		try 
		{
			inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outStream = socket.getOutputStream();
		} catch (IOException e) 
		  {
			System.err.println("ERROR @ in- /output Stream, IOException");
			e.printStackTrace();
		  }
	}
    
    /**
     * Sends a message
     * @param msg Message that shall be send
     * @throws InvalidApplicationException If the passed argument is null
     * @throws IOException In case of an error during writing the output stream
     */
    public abstract void send(String msg) throws InvalidApplicationException;
    
    /**
     * Receives a message 
     * @return returns the passed String which was read from the input Stream
     * @throws IOException In case of an error during reading the input stream
     */
    public abstract String receive();
    
    /**
     * Closes the BufferedReader and the OutputStream. Call this
     * Method if you want close the connection
     * @throws IOException
     */
    public void closeAllCom() throws IOException
    {
        inStream.close();
        outStream.close();
    } 
}
