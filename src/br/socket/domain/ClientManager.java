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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
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
	private static int descarte=0;

	
	public static void main(String[] args)  throws UnknownHostException, IOException {
		
		
		//clientSocket = new Socket("localhost", 1234);
		
		
		
		int interval = 1000;  // INTERVALO AD. MODIFICAR PARA args[0] QUANDO ESTIVER PRONTO
		Timer timer = new Timer();		
				
		timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	        	DataOutputStream dataOutputStream;
				try {
					//CASO NÃO SEJA NECESSÁRIO ESTABELECER CONEXÃO PRA CADA REQUISIÇÃO, REMOVER ISSO E ADICIONAR LÁ EM CIMA
					clientSocket = new Socket("localhost", 1234);
					dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
					//SOLICITA STATUS E RECEBE A RESPOSTA     
					dataOutputStream.writeBytes("status");
					//System.out.println("mandou");
		    		//dataOutputStream.flush();
		    		String status = readData();
		    		dataOutputStream.close();
		    		
		    	
		    		
		    		int cp = new Integer(status);
		    		System.out.println("status = " +cp);
		    		
		    		if(cp > 0) {
		    			String dados = preparaArquivo("origin.txt"); //MODIFICAR PARA args[0] QUANDO ESTIVER PRONTO
		    			dataOutputStream.writeBytes(dados); 
		    			//dataOutputStream.flush();
		    		}else {
		    			descarte++;
		    		}
		    		
		    		//SE FOR PRA ESPERAR A RESPOSTA, ADICIONAR ISSO:
		    		//System.out.println(readData());
		    		//NÃO ACHO QUE SEJA NECESSÁRIO, POIS O MODELO NÃO COBRE O TEMPO DE RETORNO DA REQUISIÇÃO
		    		
		    		//dataOutputStream.flush();
		    		//dataOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		
				
	        }
		}, 0, interval);
	}
	
	private static String preparaArquivo(String filePath)  throws IOException{
		
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
	
	private static String readData() {
	    try {
	    InputStream is = clientSocket.getInputStream();
	    ObjectInputStream ois = new ObjectInputStream(is);
	     return (String)ois.readObject();
	    } catch(Exception e) {
	        return null;
	    }   
	}
	
	private static void salvaDados(String tempoTotal ) {
		try(FileWriter fw = new FileWriter("TempoClient.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(tempoTotal);
			    
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	}
		
}
