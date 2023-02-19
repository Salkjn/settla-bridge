package de.settla.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("TransactionsCache")
public class TransactionCache implements Storable {

	private final Object lock = new Object();
	private boolean dirty;
	
	private final Map<String, Long> transactions = new HashMap<>();

	public TransactionCache() {
		
	}

	@SuppressWarnings("unchecked")
	public TransactionCache(Map<String, Object> map) {
		Map<String, Double> m = (Map<String, Double>) map.get("trans");
		m.entrySet().forEach(e -> transactions.put(e.getKey(), e.getValue().longValue()));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("trans", transactions);
		return map;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public void addTransaction(Transaction transaction) {
		synchronized (lock) {
			setDirty(true);
			Long balance = transactions.get(transaction.getAffectedAccount());
			transactions.put(transaction.getAffectedAccount(), transaction.getChange() + (balance == null ? 0 : balance));
		}
	}
	
	public void throughTransactions(Consumer<Map<String, Long>> consumer) {
		synchronized (lock) {
			consumer.accept(transactions);
		}
	}
	
}

