package com.coinbase.app;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@SpringBootApplication
public class Client {
	private final static Logger LOG = LoggerFactory.getLogger(TextWebSocketHandler.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		String socketURL = "wss://ws-feed.pro.coinbase.com";

		WebSocketClient client = new StandardWebSocketClient();
		WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, new SocketHandler(),
				socketURL);
		connectionManager.start();
		
		Thread.sleep(3500);
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the Instrument to subscribe to ");
		String instrument = scanner.nextLine();
		scanner.close();
	
		Order order = new Order(instrument);
		Broker broker = new Broker(order);
		broker.execute();
		
		SpringApplication.run(Client.class, args);
	}

}
