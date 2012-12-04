package mware_lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Benjamin Trapp Christoph Gröbke
 */
public class Communication extends ACom {
	/**
	 * Socket to handle the communication
	 */
	private Socket sock;

	/**
	 * Creates a new Communication Object
	 * 
	 * @param socket
	 *            Socket for the communication
	 */
	public Communication(Socket socket) {
		super(socket);
		this.sock = socket;
	}

	@Override
	synchronized public final void send(String marshalledMsg) {
		// Check if a moronic user tries to abuse the send()-method
		if (marshalledMsg == null)
			throw new RuntimeException("passed String is null");

		marshalledMsg += "\n"; // Append a carriage return for the
								// BufferedReader (readLine)

		try {
			super.outStream.write(marshalledMsg.getBytes());
			super.outStream.flush();
		} catch (IOException e) {
			System.err.println("ERROR @ write output stream");
			e.printStackTrace();
		}
	}

	@Override
	synchronized public final String receive() {
		StringBuffer resu = new StringBuffer();
		String line;

		try {
			// Poll to assert that everything was delivered
			while (!super.inStream.ready())
				Thread.sleep(100);

//			resu.delete(0, resu.length()-1);
			while ((line = inStream.readLine()) != null) {
				System.out.println(line);
				resu.append(line);
				if(!super.inStream.ready())
					break;
				System.out.println(resu);
			}

		} catch (Exception e) {
			System.err.println("ERROR @ read input stream (receive)");
			e.printStackTrace();
		}
		return resu.toString();
	}

	/**
	 * Get's the Host-Address of this communication back
	 * 
	 * @return String containing the Host-Address
	 */
	public String getHostAddr() {
		System.out.println("ACom @ getHostAdress");
		return sock.getInetAddress().getHostAddress();
	}

}
