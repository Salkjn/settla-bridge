package de.settla.economy;

import java.util.Map;
import java.util.Objects;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("Transaction")
public class Transaction implements Storable {

	private final String affectedAccount;
	private final String affected;
	private final long change;
	
	public Transaction(Account<?> affectedAccount, long change, boolean input) {
		this(affectedAccount.id() + "", affectedAccount.getName(), (input ? 1 : -1) * Math.abs(change));
	}
	
	private Transaction(String affected, String affectedType, long change) {
		this.affectedAccount = affectedType;
		this.affected = affected;
		this.change = change;
	}

	public Transaction(Map<String, Object> map) {
		this.affectedAccount = (String) map.get("a");
		this.affected = (String) map.get("b");
		this.change = StaticParser.parse((String) map.get("c"), Long.class);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("a", affectedAccount);
		map.put("b", affected);
		map.put("c", StaticParser.unparse(change, Long.class));
		return map;
	}
	
	public String getAffectedAccount() {
		return affectedAccount;
	}

	public String getAffected() {
		return affected;
	}

	public long getChange() {
		return change;
	}
	
	public boolean noChange() {
		return change == 0;
	}
	
	public boolean isInput() {
		return change >= 0;
	}

	public boolean isOutput() {
		return !isInput();
	}
	
	public boolean canStack(Transaction tran) {
		Objects.requireNonNull(tran);
		return tran.getAffectedAccount().equals(this.getAffectedAccount()) && tran.getAffected().equals(this.getAffected());
	}
	
	public Transaction stack(Transaction tran) {
		if(canStack(tran)) {
			return new Transaction(tran.getAffectedAccount(), tran.getAffected(), tran.getChange() + this.getChange());
		} else {
			return null;
		}
	}
}
