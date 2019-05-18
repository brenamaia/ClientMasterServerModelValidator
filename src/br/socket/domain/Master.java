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
	
	private static ArrayList<String> req = new ArrayList<String>();

	private static ServerSocket welcomeSocket;
	private static Socket connectionSocket;
	
	private static Socket clientSocket;
	
	private static String filePath;
	private static int contThread=0;
	private static int descarte=0;
	
	private String ip;
	private Integer port = 1234;

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.err.println("MASTER");

		//master.createConnection();
		
		while(true) {
			
			long start = System.currentTimeMillis();
			
			master.createConnection();
			
			//long i = System.currentTimeMillis();
			//try { Thread.sleep (1000); } catch (InterruptedException ex) {}
			//while((System.currentTimeMillis() - i) < 5000) {
			//	String z = master.receiveFile();
			//	if(z != null) {
					//req.add(master.receiveFile());
				//}
			//}
			
			
			ArrayList<String> ips = getIps(); //pega a quantidade de servidores
			
			master.receiveFile();
			
			connectionSocket.close();
			welcomeSocket.close();
			
			//i=0;
			//GERENCIAMENTO DA FILA
			contThread = req.size();
			
						
			System.out.println("contt: " + contThread);
			if(contThread > 40) {
				descarte = contThread - 40;
				contThread = 40;
				System.out.println("Descarte: " + descarte);
			}
			int a = 0;
			while(contThread > 0) {
				//System.out.println("entrou");
				if(contThread == 1) {
					String port = ips.get(0).split(";")[1];
					int p = new Integer(port);
					master.execute(req.get(a), ips.get(0).split(";")[0], p);
					contThread --;
					//clientSocket.close();
				}else {
					for(String c : ips) {
							String port = c.split(";")[1];
							int p = new Integer(port);
							master.execute(req.get(a), c.split(";")[0], p);
							contThread --;
							//clientSocket.close();
					}
				}
				a++;
			}
			req.clear();
			contThread = 0;
			descarte =0;
			
			
			
			//welcomeSocket.close();
			//connectionSocket.close();
			
		}
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
	
	public void execute(String filePath, String ip, int port) throws FileNotFoundException, IOException {
		//esse vai ser o método chamado por ClientManager
		//long start = System.currentTimeMillis();
				
		String file = preparaArquivo(filePath); //ler o arquivo
		//System.out.println("caminho: " + ip + port);
		
		createConnectionServer(ip, port); //conectar ao server
		enviarArquivo(file); //enviar arquivo para o server
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
		//System.out.println("Port "+port+" opened!");
		connectionSocket = welcomeSocket.accept();
		//System.out.println("Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());		
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
		System.out.println(readData());
	    dataOutputStream.close();
	    //clientSocket.close();
	}
	private void receiveFile() throws IOException {
		String receivedFile = null;
		
		
		long i = System.currentTimeMillis();
		
		while((System.currentTimeMillis() - i) < 5000) {
			DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());
			if (dataInputStream.available() > 0) {
				req.add(dataInputStream.readLine());
				//System.out.println("sa");
				
			}
			//dataInputStream.close();
			connectionSocket.close();
			welcomeSocket.close();
			createConnection();
		}
		
		
		
		/*
		while (true) {
			if (dataInputStream.available() > 0) {
				receivedFile = dataInputStream.readLine();
				System.out.println("received = "+receivedFile);
				
				break;
			}
		}*/
		
		
		
	}
	
	private static String readData() {
	    try {
	    InputStream is = clientSocket.getInputStream();
	    ObjectInputStream ois = new ObjectInputStream(is);
	     return (String)ois.readObject();
	    } catch(Exception e) {
	        return null;
	    }
	    
	}
}
