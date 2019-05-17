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
	
	private static int contThread =0;
	
	/*
	public ClientManager(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}*/
	
	public static void main(String[] args)  throws UnknownHostException, IOException {
		/*
		ClientManager client = new ClientManager();
		client.enviarArquivoClient("origin.txt");
		clientSocket.close();
		*/
		
		
			
		final CountDownLatch latch;
		ArrayList<String> ips =  getIps("origin.txt", "ipServidores.txt");
		String tempo = "1";
		//ArrayList<String> ips =  getIps(args[0], args[1]);
		//String tempo = args[2];
		int tempoChegada = new Integer(tempo);
		
		//final ClientManager client = null;
		latch = new CountDownLatch(ips.size());
		
		final ClientManager client = new ClientManager();
		
		for (String a : ips) {
			(new ThreadClient(latch, client)).start();
			contThread++;
		}
		/*
		
		latch = new CountDownLatch(ips.size());
		
		//envia requisições a cada tempoChegada ms
		int delay = 0;   // delay de 0ms
		int interval = 1000;  // intervalo de AD msg.
		Timer timer = new Timer();
		
		final ClientManager client = new ClientManager();
		
		timer.scheduleAtFixedRate(new TimerTask() {
		        public void run() {
		        	(new ThreadClient(latch, client)).start();
					contThread +=1; 
		        }
		    }, delay, interval);
		*/
		try {
            latch.await();  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		//try { Thread.sleep (tempoChegada); } catch (InterruptedException ex) {}
					
		
		
			//try { Thread.sleep (tempoChegada); } catch (InterruptedException ex) {}

	}
	
	private static ArrayList<String> getIps(String filePath,String propertiesFile) {
		ArrayList<String> ips =  new ArrayList<String>();
		File f = new File(propertiesFile); //ipServidores.txt
		try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String st; 
      	  	while ((st = br.readLine()) != null && st.contains(";")) {
      			ips.add(st);
      			//ips.add(st.split(";")[0], st.split(";")[1]);
      	  	}
      	  	//qtdServer = ips.size();
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
		OutputStream socketStream = clientSocket.getOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
        String dados = filePath + ";" +contThread;
        System.out.println("dados"+dados);
        
        objectOutput.writeObject(dados);
        objectOutput.close();
        socketStream.close();
        
        clientSocket.close();
		contThread = 0;
	}
	
	public void enviarArquivoClient1(String filePath) throws IOException {

		//conectando ao master
		clientSocket = new Socket("localhost", 1234); 
		
		OutputStream socketStream = clientSocket.getOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
        System.out.println("aq: "+filePath + ";"+ contThread);
        objectOutput.writeObject(filePath + ";"+ contThread);
        objectOutput.close();
        socketStream.close();
		
		/*
		DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		dataOutputStream.writeBytes(filePath + ";"+ contThread);
		System.out.println(filePath + ";"+ contThread);
		dataOutputStream.flush();		
		dataOutputStream.close();
		*/
        
		clientSocket.close();
		contThread = 0;
		
	}
	

	
	
}
