package com.coinbase.app;

import java.io.IOException;

public class Order implements Command {
	private static final OrderBook instance = OrderBook.getInstance();
	public String instrument;

	Order(String instrument) throws IOException {
		this.instrument = instrument.replaceAll("\\s+","");
	}
	
	@Override
	public void execute() throws IOException {
		instance.subscribeToInstrument(this);
	}
}
