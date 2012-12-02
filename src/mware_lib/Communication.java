package mware_lib;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Benjamin Trapp
 * 		   Christoph Gröbke
 */
public class Communication extends ACom 
{
	/**
	 * Socket to handle the communication
	 */
	private Socket sock;
	/**
	 * Creates a new Communication Object
	 * @param socket Socket for the communication
	 */
	public Communication(Socket socket) 
	{
		super(socket);
		this.sock = socket;
	}

	@Override
	public void send(String marshalledMsg)
	{
		//Check if a moronic user tries to abuse the send()-method
		if(marshalledMsg == null)
			throw new RuntimeException("passed String is null" );
		
		marshalledMsg += "\n"; //Append a carriage return for the BufferedReader (readLine)
		
		try 
		{
			super.outStream.write(marshalledMsg.getBytes());
		} catch (IOException e) {
			System.err.println("ERROR @ write output stream");
			e.printStackTrace();
		}
	}

	@Override
	public String receive()
	{
		String resu = null;
		
		try 
		{
			//Poll to assert that everything was delivered
			while(!super.inStream.ready())
				Thread.sleep(100);
			
			resu = super.inStream.readLine();
		} catch (Exception e) {
			System.err.println("ERROR @ read input stream (receive)");
			e.printStackTrace();
		}

		return resu;
	}
	
    /**
     * Get's the Host-Address of this communication back
     * @return String containing the Host-Address
     */
    public String getHostAddr()
    {
    	System.out.println("ACom @ getHostAdress");
    	return sock.getInetAddress().getHostAddress();
    }
	
}
