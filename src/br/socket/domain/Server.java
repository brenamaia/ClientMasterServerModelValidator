package br.socket.domain;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.utils.WordCount;

public class Server {
	private ServerSocket welcomeSokect;
	private Socket connectionSocket;
	private Integer port = 12345;
	
	/*
	 * Don't forget to pass the port as parameter
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.err.println("SERVER");
		Server serv = new Server();
		serv.createConnection();
		String receiveFile = serv.receiveFile();
		long start = System.currentTimeMillis();
		String wordCount = WordCount.wordCount(receiveFile);
		System.out.println("Processing Time (ms): " + (System.currentTimeMillis() - start));
		serv.returnResultToClient(wordCount);
	}

	private String receiveFile() throws IOException {
		String receivedFile = null;
		DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());
		while (true) {
			if (dataInputStream.available() > 0) {
				receivedFile = dataInputStream.readLine() + "\n";
				break;
			} 
		}
		return receivedFile;
	}

	private void createConnection() throws IOException {
		welcomeSokect = new ServerSocket(port);
		System.out.println("Port "+port+" opened!");
		connectionSocket = welcomeSokect.accept();
		System.out.println("Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());
	}

	private void returnResultToClient(String result) throws IOException {
		OutputStream socketStream = connectionSocket.getOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
        objectOutput.writeObject(result);
        objectOutput.close();
        socketStream.close();
	}

	
	

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	
	
	
}