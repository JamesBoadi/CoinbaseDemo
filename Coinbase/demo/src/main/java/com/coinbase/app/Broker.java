package com.coinbase.app;

import java.io.IOException;

public class Broker {
	private Order order;

	public Broker(Order order) {
		this.order = order;
	}

	public void execute() throws IOException {
		this.order.execute();
	}
}
