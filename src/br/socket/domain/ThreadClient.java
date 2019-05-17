package br.socket.domain;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

class ThreadClient extends Thread {
    private CountDownLatch latch;
	private ClientManager client;

    public ThreadClient(CountDownLatch latch, ClientManager client) {
        this.latch = latch;
        this.client = client;
    }

    public void run() {
        Random rand = new Random();
        int n = rand.nextInt(50) + 1;
        System.out.println("Thread "+n+" Started.");

		try {
			client.enviarArquivoClient("origin.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		System.out.println("Thread "+n+" has finished.");

        latch.countDown();
    }
}