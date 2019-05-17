package br.socket.domain;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

class ThreadClient extends Thread {
    private CountDownLatch latch;
	private ClientManager client;
	private String filePath;

    public ThreadClient(CountDownLatch latch, ClientManager client, String filePath) {
        this.latch = latch;
        this.client = client;
        this.filePath = filePath;
    }

    public void run() {
        Random rand = new Random();
        int n = rand.nextInt(40) + 1;
        System.out.println("Thread "+n+" Started.");

		try {
			client.enviarArquivoClient(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		System.out.println("Thread "+n+" has finished.");

        latch.countDown();
    }
}