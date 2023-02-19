package de.settla.utilities.sakko;

import java.util.Objects;

public class SakkoAddress {

	private final String host;
	private final int port;

	public SakkoAddress(String host, int port) {
		this.host = Objects.requireNonNull(host);
		this.port = Objects.requireNonNull(port);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
}
