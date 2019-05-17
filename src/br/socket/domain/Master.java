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
import java.util.concurrent.CountDownLatch;

import br.utils.WordCount;

public class Master {

	private static ServerSocket welcomeSocket;
	private static Socket connectionSocket;
	
	private static Socket clientSocket;
	
	private static String filePath;
	private static int contThread;
	
	
	private String ip;
	private Integer port = 1234;

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.err.println("MASTER");
		
		while(true) {
			Master master = new Master();
			long start = System.currentTimeMillis();
			master.createConnection();
			String receiveFile = master.readDataClient();		
			System.out.println("Arquivo recebido no master: " + receiveFile);
			
			String conta = receiveFile.split(";")[1];
			contThread = new Integer(conta);
			master.execute(receiveFile.split(";")[0]);
			welcomeSocket.close();
			connectionSocket.close();
		}
		//clientSocket.close();
		/*
		double tempoTotal = System.currentTimeMillis() - start;
		System.out.println("Processing Time (ms): " + tempoTotal);
		salvaTempos(tempoTotal+"");
		*/
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
	

	public void execute(String filePath) throws FileNotFoundException, IOException {
		//esse vai ser o método chamado por ClientManager
		//long start = System.currentTimeMillis();
		
		//int cont=0;
		//while(cont<1) {
		ArrayList<String> ips = getIps();
		ArrayList<Integer> req = new ArrayList();
		
		int qtdServer = ips.size();
		int cont=0;
		int frenteFila = 1;
		while (contThread > 0) {
			
			for(String a : ips) {
				
				String file = preparaArquivo(filePath); //ler o arquivo
				System.out.println("caminho: " + a.split(";")[0] + a.split(";")[1]);
				String p = a.split(";")[1];
			
				
				int portS = new Integer(p);
				createConnectionServer(a.split(";")[0], portS); //conectar ao server
				enviarArquivo(file); //enviar arquivo para o server
				
				contThread -= qtdServer;
			}
		}
			/*
			ArrayList<String> caminhos = new ArrayList();
			caminhos = getIps();
			
			String port = st.split(";")[1];
			Integer p = new Integer(port);
			clients.add(new Client(st.split(";")[0], p,filePath));
			
			int i = 0;
			
		    for (String st: caminhos) {
		    	String port = st.split(";")[1];
		    	Integer p = new Integer(port);
		    	createConnectionServer();
		    	
		    	//if(serv.usageCPU() < 10) {
		    	//System.out.println("Enviado pro servidor: " + st.split(";")[0] + "/" + p);
		    	enviarArquivo(file);
		    	
		    	//}
		    	//clientSocket.close();
		    	//System.out.printf("Posição %d- %s\n", i, contato);
		        //i++;
		    }
		    cont++;
		    
		    //try { Thread.sleep (5000); } catch (InterruptedException ex) {}
		//}		
		 //adicionar parâmetros de acordo com o balanceamento de carga
		  * */
		 
	}
	
	private static ArrayList<String> getIps() {
		ArrayList<String> ips = new ArrayList();
		File f = new File("ipServidores.txt");
		try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String st; 
      	  	while ((st = br.readLine()) != null && st.contains(";")) {
      			ips.add(st);
      	  	}
      	  	//qtdServer = ips.size();
        } catch (IOException e) {
            System.out.println("###### Erro: "+e.getMessage());
            e.printStackTrace();
        }
		
		return ips;
		
	}

	private void createConnection() throws IOException {
		welcomeSocket = new ServerSocket(port);
		System.out.println("Port "+port+" opened!");
		connectionSocket = welcomeSocket.accept();
		System.out.println("Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());
	}
	
	private void createConnectionServer(String ipS, int portS) throws IOException {
		clientSocket = new Socket(ipS, portS);
	}

	private String preparaArquivo(String filePath)  throws IOException{
		
		BufferedReader arqBuffer;
		ArrayList<String> linhasArray = new ArrayList<>();
		String linha;

		arqBuffer = new BufferedReader(new FileReader(filePath));
		linha = arqBuffer.readLine() + "\n";
		linhasArray.add(linha);

		while (linha != null) {
			linha = arqBuffer.readLine();
			linhasArray.add(linha);
		}
		arqBuffer.close();

		linhasArray.remove(linhasArray.size() - 1);
		linha = linhasArray.get(0);

		for (int i = 1; i < linhasArray.size(); i++) {
			linha += linha = linhasArray.get(i) + "\n";
		}
		return linha;
	}
	
	private void enviarArquivo(String file) throws IOException {
		
		DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		dataOutputStream.writeBytes(file);
		dataOutputStream.flush();

		//DataInputStream c = new DataInputStream(clientSocket.getInputStream());
	    //System.err.println("The result from server was: \n" + readData());

	    dataOutputStream.close();
	    //clientSocket.close();
	}
	
	private String receiveFile() throws IOException {
		String receivedFile = null;
		DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());

		while (true) {
			if (dataInputStream.available() > 0) {
				receivedFile = dataInputStream.readLine();
				System.out.println(receivedFile);
				
				break;
			} 
		}
		connectionSocket.close();
		welcomeSocket.close();
		
		Master master = new Master();
		
		System.out.println("final: "+ receivedFile);
		
		System.out.println("caminho: "+ receivedFile.split(";")[0] + receivedFile.split(";")[1]);
		//filePath = receivedFile.split(";")[0];
		String ct = receivedFile.split(";")[1];
		contThread = new Integer(ct);
		
		return receivedFile.split(";")[0];
	}
	
	
	
	private String readDataClient() {
	    try {
	    InputStream is = connectionSocket.getInputStream();
	    ObjectInputStream ois = new ObjectInputStream(is);
	     return (String)ois.readObject();
	    } catch(Exception e) {
	        return null;
	    }
	    
	}
	
	private void returnResultToClient(String result) throws IOException {
		OutputStream socketStream = connectionSocket.getOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
        objectOutput.writeObject(result);
        objectOutput.close();
        socketStream.close();
	}
	
	@Override
	public String toString() {
		return "CLIENT CONFIGURED WITH ... server ip: " + this.ip +" server port: " + this.port;
	}

}
