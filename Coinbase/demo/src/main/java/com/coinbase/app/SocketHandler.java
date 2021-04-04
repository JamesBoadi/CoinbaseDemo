package com.coinbase.app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketHandler extends TextWebSocketHandler {
	private static final Logger LOG = LoggerFactory.getLogger(TextWebSocketHandler.class);

	double prevPrice = Integer.MIN_VALUE;
	private static final OrderBook instance = OrderBook.getInstance();
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		// Receive messages from WebSocket feed
		try {
			// Process message convert to JSON
			JsonObject jsonObject = JsonParser.parseString(message.getPayload()).getAsJsonObject();
			String type = jsonObject.get("type").getAsString();
			
			if (type.equals("ticker") && instance.UPDATE_ORDER_BOOK == false) {
				/* If the previous price is not equal to the current price
				 * the OrderBook will be cleared and populated with new
				 * entries of bids and asks (l2update). Ticker automatically
				 * updates bids and asks through a persistent connection 
				 */
				double currentPrice = jsonObject.get("price").getAsDouble();
				if (this.prevPrice != currentPrice ) // Update based on tick
				{
					instance.clearOrderBook();
					this.prevPrice = currentPrice;
					instance.setUpdateOrderBook(true);
					LOG.info("Tick");
				}
			} else {
				if (instance.UPDATE_ORDER_BOOK) {
					instance.setOrderBook(message.getPayload());
				}
			}
		} catch (JsonSyntaxException ex) {
			LOG.info("Bad JSON, Just a sec... Skipping... ");
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		new ServiceWorker(session);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		LOG.error("Transport Error", exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		LOG.info("Connection Closed [" + status.getReason() + "]");
	}

	@Override
	public boolean supportsPartialMessages() {
		return true;
	}
}
