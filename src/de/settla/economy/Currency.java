package de.settla.economy;

public class Currency {

	private final String name;
	private final String symbol;

	public Currency(String name, String symbol) {
		super();
		this.name = name;
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}
	
}
