package br.socket.domain;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class is responsible by managing the pool of clients which send files to be processed by servers.
 * Each client is configured to send a request to an specific server.
 * The ip and port of the servers are disposed into the file server_ips.properties.
 * The inSequenceExecution means that only one server will be available at a time --- this is equivalent to have only one available server.
 * @author Airton
 *
 */
public class ClientManager {

	private static Socket clientSocket;
		
	
	public static void main(String[] args)  throws UnknownHostException, IOException {
		
		ClientManager client = new ClientManager();
		client.enviarArquivoClient("origin.txt");
		clientSocket.close();
	}
	
	
	public int enviarArquivoClient(String filePath) throws IOException {

		clientSocket = new Socket("localhost", 1234);
		
		DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		dataOutputStream.writeBytes(filePath);
		dataOutputStream.flush();		
		dataOutputStream.close();
		
		return 1;
	}
	
	private String readData() {
	    try {
	    InputStream is = clientSocket.getInputStream();
	    ObjectInputStream ois = new ObjectInputStream(is);
	     return (String)ois.readObject();
	    } catch(Exception e) {
	        return null;
	    }
	}
	
	
}
