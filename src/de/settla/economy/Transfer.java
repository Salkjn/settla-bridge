package de.settla.economy;

import java.util.Map;
import java.util.Objects;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("Transfer")
public class Transfer implements Storable {

	private TransferFailure failure;
	private long startFrom, startTo;
	private long endFrom, endTo;
	private long change;
	
	public Transfer() {
		this.failure = TransferFailure.NOTHING;
	}
	
	public Transfer(Map<String, Object> map) {
		this.failure = TransferFailure.values()[StaticParser.parse((String) map.get("failure"), Integer.class)];
		this.startFrom = StaticParser.parse((String) map.get("startFrom"), Long.class);
		this.startTo = StaticParser.parse((String) map.get("startTo"), Long.class);
		this.endFrom = StaticParser.parse((String) map.get("endFrom"), Long.class);
		this.endTo = StaticParser.parse((String) map.get("endTo"), Long.class);
		this.change = StaticParser.parse((String) map.get("change"), Long.class);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("failure", StaticParser.unparse(failure.ordinal(), Integer.class));
		map.put("startFrom", StaticParser.unparse(startFrom, Long.class));
		map.put("startTo", StaticParser.unparse(startTo, Long.class));
		map.put("endFrom", StaticParser.unparse(endFrom, Long.class));
		map.put("endTo", StaticParser.unparse(endTo, Long.class));
		map.put("change", StaticParser.unparse(change, Long.class));
		return map;
	}
	
	public void failure(TransferFailure failure) {
		Objects.requireNonNull(failure);
		this.failure = failure;
	}

	public void success() {
		failure(TransferFailure.SUCCESS);
	}
	
	public boolean isSuccess() {
		return failure == TransferFailure.SUCCESS;
	}
	
	public void start(long startFrom, long startTo) {
		this.failure = TransferFailure.NOTHING;
		this.startFrom = startFrom;
		this.startTo = startTo;
	}
	
	public void change(long endFrom, long endTo) {
		this.endFrom = endFrom;
		this.endTo = endTo;
		this.change = endTo - startTo;
	}
	
	public long change() {
		return this.change;
	}
	
	public long startFrom() {
		return startFrom;
	}

	public long endFrom() {
		return endFrom;
	}
	
	public long startTo() {
		return startTo;
	}

	public long endTo() {
		return endTo;
	}
	
	public TransferFailure getFailure() {
		return failure;
	}
	
	public enum TransferFailure {
		
		NOTHING, UNREGISTERED_USER, UNREGISTERED_ACCOUNT_HANDLER, FROM_MAXIMAL_REACHED, FROM_MINIMAL_REACHED, TO_MAXIMAL_REACHED, TO_MINIMAL_REACHED, FILL_UP_TO_TOP, FILL_UP_TO_BOTTOM, SUCCESS;
		
	}
	
}
