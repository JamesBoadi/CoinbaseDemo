package com.coinbase.app;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.w3c.dom.css.Counter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class OrderBook {

	private static OrderBook instance = null;

	private static List<OrderBook> orderBook = new ArrayList<>();

	private static List<Order> orders = new ArrayList<>();

	private final Logger LOG = LoggerFactory.getLogger(TextWebSocketHandler.class);

    /** A Boolean for determining whether or not the OrderBook should update {@value #UPDATE_ORDER_BOOK}. */
	public boolean UPDATE_ORDER_BOOK = false;

	String instrument;
	String side;
	double price;
	double size;
	
	/** An Integer that keeps track of the OrderBook size {@value #pointer}. */
	static int pointer = 0;

	private OrderBook(String instrument, String side, double price, double size) {
		this.instrument = instrument;
		this.side = side;
		this.price = price;
		this.size = size;

		orderBook.add(this);
		LOG.info("Instrument: " + instrument + "  Side: " + side + "  Price: " + price + "  Size: " + size);
	}

	/**
	* Creates a Singleton of OrderBook Class
	*
	* @return A singleton instance
	*/
	public static OrderBook getInstance() {
		synchronized (OrderBook.class) {
			if (instance == null) {
				instance = new OrderBook(null, null, Integer.MIN_VALUE, Integer.MIN_VALUE);
			}
		}
		return instance;
	}

	
	// **************************************************
    // Public methods
    // **************************************************
	
	/**
	* Updates the OrderBook
	*
	* @return void
	*/
	public void setOrderBook(String message) {
		try {
			// Parse message into JSON 
			JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
			JsonArray changes = jsonObject.get("changes").getAsJsonArray();
			JsonElement propertiesJson = changes.get(0);
			JsonArray object = propertiesJson.getAsJsonArray();

			String instrument = jsonObject.get("product_id").getAsString();
			String side = object.get(0).getAsString();
			double price = object.get(1).getAsDouble();
			double size = object.get(2).getAsDouble();

			// Maintain 10 levels of an OrderBook
			if (pointer != 10) {
				new OrderBook(instrument, side, price, size);
				pointer++;
			} else if (orderBook.size() != 10) {
				pointer = orderBook.size();
			} else {
				this.UPDATE_ORDER_BOOK = false;
				pointer = 0;
			}
		} catch (JsonSyntaxException | java.lang.NullPointerException ex) {
			LOG.info("Bad JSON, Just a sec... Skipping... ");
		}
	}

	/**
	* Clears the OrderBook
	*
	* @return void
	*/
	public void clearOrderBook() {
		orderBook.clear();
	}
	
	/**
	* Gets Boolean that determines whether or not the OrderBook should be updated
	*
	* @return get boolean for updating order book
	*/
	public boolean getUpdateOrderBook() {
		return this.UPDATE_ORDER_BOOK;
	}

	/**
	* Sets Boolean that determines whether or not the OrderBook should be updated
	*
	*/
	public void setUpdateOrderBook(boolean updateOrderBook) {
		this.UPDATE_ORDER_BOOK = updateOrderBook;
	}

	/**
	* Subscribes to an instrument using a websocket
	*
	*/
	public void subscribeToInstrument(Order order) throws IOException
	{
		orders.clear(); 
		orders.add(order);
		ServiceWorker.subscribeToInstrument();
	}
	
	// ******************************
	
	// ******************************
	// Helper Functions
	// ******************************
	/**
	* Escapes a string
	* 
	* @note cannot escape strings outside of UTF-16 
	*
	* @return An escaped string
	*/
	public String escapeString(String string) {
		return string.replaceAll("\"", "\\\\");
	}
	
	/**
	* Gets an OrderBook
	*
	* @return a list containing bids and asks
	*/
	public List<OrderBook> getOrderBook() {
		return orderBook;
	}

	/**
	* Gets an order
	*
	* @return a list of orders
	*/
	public List<Order> getOrders()
	{
		return orders;
	}
	
	// ******************************

}
