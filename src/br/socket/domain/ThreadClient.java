package br.socket.domain;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

class ThreadClient extends Thread {
    private CountDownLatch latch;
    Socket clientSocket;
	private String ip;
	private int p;
	private int h;
	private String dados;
	private Master master;

    public ThreadClient(String ip, int p, String dados, int h) {
        this.ip = ip;
        this.p = p;
        this.dados = dados;
        this.h = h;
    }

    public void run() {
        Random rand = new Random();
        int n = rand.nextInt(40) + 1;
        System.out.println("Thread "+n+" Started.");

		try {
			clientSocket = new Socket(ip, p);
			
			DataOutputStream dataOutputStream;
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			dataOutputStream.writeBytes(dados);
			dataOutputStream.flush();
			dataOutputStream.close();
			
			master.atualizaCapacidade(ip, p, h);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		System.out.println("Thread "+n+" has finished.");

        latch.countDown();
    }
}