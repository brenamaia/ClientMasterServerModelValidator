package br.socket.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
	
	private int port;
	private String ip; 
	private String filePath;
	private static String tempoChegada;
	
	//private static int contThread =0;
	
	/*
	public ClientManager(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}*/
	
	public static void main(String[] args)  throws UnknownHostException, IOException {
		
		
		
		final CountDownLatch latch;
		
		ArrayList<String> ips =  getIps("ipServidores.txt");
		String tempo = "1000";
		
		
		latch = new CountDownLatch(1);
		
		//envia requisições a cada tempoChegada ms
		int delay = 0;   // delay de 0ms
		int interval = 1000;  // intervalo de AD msg.
		Timer timer = new Timer();		
		
		final ClientManager client = new ClientManager();
		
		timer.scheduleAtFixedRate(new TimerTask() {
		        public void run() {
		        	(new ThreadClient(latch, client, "origin.txt")).start();
					//contThread +=1; 
		        }
		    }, 0, interval);
		/*
		try {
            latch.await();  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
		
	}
	
	private static ArrayList<String> getIps(String propertiesFile) {
		ArrayList<String> ips =  new ArrayList<String>();
		File f = new File(propertiesFile); //ipServidores.txt
		try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String st; 
      	  	while ((st = br.readLine()) != null && st.contains(";")) {
      			ips.add(st);
      	  	}
        } catch (IOException e) {
            System.out.println("###### Erro: "+e.getMessage());
            e.printStackTrace();
        }
		return ips;	
	}
	
	private static void salvaTempos(String tempoTotal ) {
		try(FileWriter fw = new FileWriter("TempoClient.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(tempoTotal);
			    
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	}
	

	public void enviarArquivoClient(String filePath) throws IOException {
		
		clientSocket = new Socket("localhost", 1234);
		DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		dataOutputStream.writeBytes(filePath);
		System.out.println(filePath);
		dataOutputStream.flush();		
		dataOutputStream.close();
		clientSocket.close();
		
	}
		
}
