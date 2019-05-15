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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import br.utils.WordCount;

public class Master {

	private ServerSocket welcomeSokect;
	private Socket connectionSocket;
	
	private Socket clientSocket;
	
	private String ip;
	private Integer port = 1234;
	private static String resultado;

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.err.println("MASTER");
		
		Master master = new Master();
		long start = System.currentTimeMillis();
		master.createConnection();
		String receiveFile = master.receiveFile();				
		master.execute(receiveFile);
		
		System.out.println("Processing Time (ms): " + (System.currentTimeMillis() - start));

	}
	

	public void execute(String filePath) throws FileNotFoundException, IOException {
		//esse vai ser o método chamado por ClientManager
		
		String file = preparaArquivo(filePath);
		
		//chamada de método de balanceamento de carga aqui
		
		createConnectionServer("localhost", 12345); //adicionar parâmetros de acordo com o balanceamento de carga
		
		enviarArquivo(file);
	}


	private void createConnection() throws IOException {
		welcomeSokect = new ServerSocket(port);
		System.out.println("Port "+port+" opened!");
		connectionSocket = welcomeSokect.accept();
		System.out.println("Server: new connection with client: " + connectionSocket.getInetAddress().getHostAddress());
	}
	
	private void createConnectionServer(String ip, Integer portS) throws IOException {
		clientSocket = new Socket(ip, portS);
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

		DataInputStream c = new DataInputStream(clientSocket.getInputStream());
	    System.err.println("The result from server was: \n" + readData());
	    resultado = readData();
	    dataOutputStream.close();
	    
	}
	
	private String receiveFile() throws IOException {
		String receivedFile = null;
		DataInputStream  dataInputStream = new DataInputStream(connectionSocket.getInputStream());

		while (true) {
			if (dataInputStream.available() > 0) {
				receivedFile = dataInputStream.readLine();
				break;
			} 
		}
		return receivedFile;
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
