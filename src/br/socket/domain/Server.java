package br.socket.domain;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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
	private static ServerSocket welcomeSocket;
	private static Socket connectionSocket;
	private Integer port = 12345;
	
	/*
	 * Don't forget to pass the port as parameter
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.err.println("SERVER");
		Server serv = new Server();
		//serv.createConnection();
		
	
		int cont =0;
		while(true) {
			serv.createConnection();
			String receiveFile = serv.receiveFile();
			long start = System.currentTimeMillis();
			String wordCount = WordCount.wordCount(receiveFile);
			double tempoTotal = System.currentTimeMillis() - start;
			System.out.println(wordCount);
			System.out.println("Processing Time (ms): " + tempoTotal);
			salvaTempos(tempoTotal+"");
			
			//serv.returnResultToClient(wordCount);
			connectionSocket.close();
			welcomeSocket.close();
			cont++;
			//try { Thread.sleep (2000); } catch (InterruptedException ex) {}
		}
	}
	
	public double usageCPU() {
		final OperatingSystemMXBean myOsBean=  ManagementFactory.getOperatingSystemMXBean();
		double load = myOsBean.getSystemLoadAverage();
		
		return load;
	}
	
	private static void salvaTempos(String tempoTotal ) {
		
		try(FileWriter fw = new FileWriter("TempoServidor.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(tempoTotal);
			    
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
		
	
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
		welcomeSocket = new ServerSocket(port);
		System.out.println("Port "+port+" opened!");
		connectionSocket = welcomeSocket.accept();
		System.out.println("Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());
	}

	private void returnResultToClient(String result) throws IOException {
		OutputStream socketStream = connectionSocket.getOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
        System.out.println(result);
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