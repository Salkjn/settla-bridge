package de.settla.economy;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;
//Niklas hat Abzug
abstract public class Account<T> implements Storable {

	public final static int MAXIMAL_TRANSACTIONS = 30;
	
	private final T id;

	private long wrappedBalance;
	private long minimumBalance;
	private long maximumBalance;

	private final Object lock = new Object();
	
	private boolean dirty;

	private final TransactionCache cache;
	private final Stack<Transaction> transactions = new Stack<>();
	
	public Account(T id, long minimumBalance, long maximumBalance) {
		this.id = id;
		this.minimumBalance = minimumBalance;
		this.maximumBalance = maximumBalance;
		this.cache = new TransactionCache();
	}
	
	@SuppressWarnings("unchecked")
	public Account(T id, Map<String, Object> map) {
		this.id = id;
		this.minimumBalance = StaticParser.parse((String) map.get("min"), Long.class);
		this.maximumBalance = StaticParser.parse((String) map.get("max"), Long.class);
		this.wrappedBalance = StaticParser.parse((String) map.get("bal"), Long.class);
		
		((List<Map<String, Object>>) map.get("trans")).forEach(m -> transactions.add(deserialize(m, Transaction.class)));
		this.cache = deserialize(map.get("cache"), TransactionCache.class);
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("min", StaticParser.unparse(minimumBalance, Long.class));
			map.put("max", StaticParser.unparse(maximumBalance, Long.class));
			map.put("bal", StaticParser.unparse(wrappedBalance, Long.class));
			map.put("trans", transactions.stream().map(tran -> tran.serialize()).collect(Collectors.toList()));
			map.put("cache", cache.serialize());
			return map;
		}
	}

	public abstract String getName();
	
	public T id() {
		return id;
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return dirty || cache.isDirty();
		}
	}
	
	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			this.dirty = dirty;
			this.cache.setDirty(dirty);
		}
	}
	
	public final void addWrappedBalance(long wrappedBalance) {
		synchronized (lock) {
			this.dirty = true;
			this.wrappedBalance += wrappedBalance;
		}
	}
	
	public long getWrappedBalance() {
		synchronized (lock) {
			return wrappedBalance;
		}
	}

	public void setWrappedBalance(long wrappedBalance) {
		synchronized (lock) {
			this.dirty = true;
			this.wrappedBalance = wrappedBalance;
		}
	}

	public long getMinimumBalance() {
		synchronized (lock) {
			return minimumBalance;
		}
	}

	public void setMinimumBalance(long minimumBalance) {
		synchronized (lock) {
			this.dirty = true;
			this.minimumBalance = minimumBalance;
		}
	}

	public long getMaximumBalance() {
		synchronized (lock) {
			return maximumBalance;
		}
	}

	public void setMaximumBalance(long maximumBalance) {
		synchronized (lock) {
			this.dirty = true;
			this.maximumBalance = maximumBalance;
		}
	}
	
	public final boolean toHight(long check) {
		return check > maximumBalance;
	}

	public final boolean toLow(long check) {
		return minimumBalance > check;
	}
	
	public final boolean isUnlimitedAccount() {
		return this instanceof UnlimitedAccount;
	}
	
	public void throughTransactions(Consumer<Stack<Transaction>> consumer) {
		synchronized (lock) {
			consumer.accept(transactions);
		}
	}
	
	protected final void addTransaction(Transaction transaction) {
		cache.addTransaction(transaction);
		throughTransactions(stack -> {
			if (!stack.isEmpty()) {
				Transaction last = stack.peek();
				if (last.canStack(transaction)) {
					Transaction newTransaction;
						newTransaction = last.stack(transaction);
						if(newTransaction.noChange()) {
							stack.pop();
						} else {
							stack.set(stack.size() - 1, newTransaction);
						}
				} else {
					stack.add(transaction);
				}
			} else {
				stack.add(transaction);
			}
			if (stack.size() > MAXIMAL_TRANSACTIONS)
				stack.remove(0);
		});
	}
	
}
