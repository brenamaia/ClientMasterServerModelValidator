package br.socket.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import br.utils.WordCount;

public class Master {
	
	static Master master = new Master();
	
	private static ServerSocket welcomeSocket;
	private static Socket connectionSocket;
	
	private static Socket clientSocket;
	
	private static int capacidade=40;
	private static int ocupadosM=0;
	private static int descarte=0;
	
	private static int capacidadeS=0;
	private static int ocupadosS=0;
	private static ArrayList<String> ips = getIps(); //pega a quantidade de servidores
	
	//ArrayList<String> req = new ArrayList();
	
	private Integer port = 1234;

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.err.println("MASTER");

		//master.createConnection();
		
		
		
		capacidadeS = ips.size();
				
		while(true) {
			
			long start = System.currentTimeMillis();
			
			master.createConnection();
			
			String msg = master.receive();
			
			if(msg.equals("status")) {
				
				returnResultToClient((capacidade-ocupadosM) + "");
			}
			
			if((capacidade-ocupadosM) > 0) {
				String req = master.receive();
				ocupadosM++;
				//CASO NÃO SEJA NECESSÁRIO ESTABELECER CONEXÃO PRA CADA REQUISIÇÃO, REMOVER ISSO:
				connectionSocket.close();
				welcomeSocket.close();
				
				//DataOutputStream dataOutputStream = null;
				int h=0;
	    		if(ocupadosM <= capacidadeS){
	    			while(ocupadosM > 0) {
	    				if(ips.get(h).split(";")[2] == "disponivel") {
	    					String porta =  ips.get(h).split(";")[1];
	    					int p = new Integer(porta);
	    					new ThreadClient(ips.get(h).split(";")[0], p, req, h).start();
			    			ocupadosM--;
			    			ips.remove(h);
			    			ips.add(ips.get(h).split(";")[0] + ";" + p + ";" + "indisponivel");
			    			h++;
	    				}else {
	    					h++;
	    				}
	    			}
	    		}else {
	    			while(capacidadeS > 0) {
	    				if(ips.get(h).split(";")[2] == "disponivel") {
	    					String porta =  ips.get(h).split(";")[1];
	    					int p = new Integer(porta);
	    					new ThreadClient(ips.get(h).split(";")[0], p, req, h).start();
			    			ocupadosM--;
			    			ips.remove(h);
			    			ips.add(ips.get(h).split(";")[0] + ";" + p + ";" + "indisponivel");
			    			h++;
	    				}else {
	    					h++;
	    				}
	    			}
	    		}
	    		
	    		String q = readDataServer();
	    		
	    		//SE FOR PRA ENVIAR A RESPOSTA PARA O CLIENTE, ADICIONAR ISSO E NÃO FECHAR A CONEXÃO ACIMA:
	    		//returnResultToClient(readDataServer());
	    		//NÃO ACHO QUE SEJA NECESSÁRIO, POIS O MODELO NÃO COBRE O TEMPO DE RETORNO DA REQUISIÇÃO
	    		
	    		master.createConnection();
			}
			
		}
	}
	
	static void atualizaCapacidade(String ip, int port, int h) {
		ips.remove(h);
		ips.add(ips.get(h).split(";")[0] + ";" + port + ";" + "disponivel");
	}
	
	private static ArrayList<String> getIps() {
		ArrayList<String> ips = new ArrayList();
		File f = new File("ipServidores.txt");
		try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String st; 
      	  	while ((st = br.readLine()) != null && st.contains(";")) {
      			ips.add(st + ";disponivel");
      	  	}
      	  	//qtdServer = ips.size();
        } catch (IOException e) {
            System.out.println("###### Erro: "+e.getMessage());
            e.printStackTrace();
        }
		
		return ips;
		
	}
	
	public static void returnResultToClient(String result) throws IOException {
		OutputStream socketStream = connectionSocket.getOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
        
        objectOutput.writeObject(result);
        objectOutput.close();
        socketStream.close();
	}
	
	private static void salvaTempos(String tempoTotal ) {
		try(FileWriter fw = new FileWriter("TempoMaster.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(tempoTotal);
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	}
	
	private void createConnection() throws IOException {
		welcomeSocket = new ServerSocket(port);
		System.out.println("Port "+port+" opened!");
		connectionSocket = welcomeSocket.accept();
		System.out.println("Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());		
	}
	
	private String receive() throws IOException {
		String received = null;
				
		DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());
		
		while(true) {
			if (dataInputStream.available() > 0) {
				received = dataInputStream.readLine();
				System.out.println(received);
				return received;
			}
		}
		//dataInputStream.close();
	}
	
	private static String readDataServer() {
	    try {
	    InputStream is = clientSocket.getInputStream();
	    ObjectInputStream ois = new ObjectInputStream(is);
	     return (String)ois.readObject();
	    } catch(Exception e) {
	        return null;
	    }
	    
	}
}
