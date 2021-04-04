package com.coinbase.app;

import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class ServiceWorker {
	private static final OrderBook instance = OrderBook.getInstance();
	private static WebSocketSession session;
	private static final Logger LOG = LoggerFactory.getLogger(TextWebSocketHandler.class);
	
	/**
	* Creates a websocket session
	*/
	public ServiceWorker(WebSocketSession _session) throws IOException {
		
		if (_session.isOpen()) {
			session = _session;
		}
		else
		{
			LOG.info("Connection Faliure");
			session.close();
			System.exit(0);
		}
	}


	/**
	* Subscribes to an instrument using a websocket
	*
	*@note This method does not throw an exception for invalid syntax when processing instruments.
	*If an instrument is not found the connection will remain open, but
	*no further information will be displayed
	*
	*@exception IOException
	*
	*/
	public static void subscribeToInstrument() throws IOException
	{
		List<Order> order = instance.getOrders();
		String instrument = instance.escapeString(order.get(0).instrument);
		LOG.info("Subscribing to Instrument.... Just a sec... -> " + instrument);
		LOG.info("If you entered a valid Instrument,\n it should display automatically, if not restart application");
		
		// Subscribe
		String payload = "{\"type\": \"subscribe\",\"channels\":[{\"name\": \"ticker\",\"product_ids\": [\""+ instrument +"\"]},"
			+ "{\"name\": \"level2\",\"product_ids\": [\""+ instrument +"\"]}]}";

		// Send a message to the server
		session.sendMessage(new TextMessage(payload));
	}
	
	
}
