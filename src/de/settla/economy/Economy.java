package de.settla.economy;

import de.settla.utilities.functions.BijectiveFunction;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public abstract class Economy {
	
	private final BijectiveFunction<Double, Long> wrapper = new BijectiveFunction<Double, Long>(a -> (long) (a * 100.0), b -> ((double) b) / 100D);
	
	private final SakkoProtocol protocol;
	private final Currency currency;
	
	public Economy(SakkoProtocol protocol, Currency currency) {
		super();
		this.protocol = protocol;
		this.currency = currency;
	}

	public BijectiveFunction<Double, Long> getWrapper() {
		return wrapper;
	}

	public SakkoProtocol getProtocol() {
		return protocol;
	}

	public Currency getCurrency() {
		return currency;
	}
	
}
