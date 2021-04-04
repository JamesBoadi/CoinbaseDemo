package com.coinbase.app;

import java.io.IOException;

public interface Command {
	public abstract void execute() throws IOException;
}
