package de.settla.utilities.sakko.protocol;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("Answer")
public class SakkoAnswer extends SakkoConversation {

	private final UUID id;
	private final Map<String, Object> answer;
	
	protected SakkoAnswer(UUID id, Map<String, Object> answer) {
		this.id = Objects.requireNonNull(id);
		this.answer = Objects.requireNonNull(answer);
	}
	
	public SakkoAnswer(SakkoQuestion question, Map<String, Object> answer) {
		this(question.getId(), answer);
	}

	@SuppressWarnings("unchecked")
	public SakkoAnswer(Map<String, Object> map) {
		this(StaticParser.parse((String)map.get("id"), UUID.class), (Map<String, Object>) map.get("answer"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("id", id.toString());
		map.put("answer", answer);
		return map;
	}

	public UUID getId() {
		return id;
	}

	public <T> SakkoAnswer put(String key, T value, Class<T> clazz) {
		answer.put(key, StaticParser.unparse(value, clazz));
		return this;
	}
	
	public SakkoAnswer put(String key, Storable data) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(data);
		answer.put(key, data == null ? null : data.serialize());
		return this;
	}

	public <T> T getAnswer(String key, Class<T> clazz) {
		Object value = answer.get(key);
		if(value != null) {
			 return StaticParser.parse((String)value, clazz);
		} else {
			return null;
		}
	}
	
	public <T extends Storable> T getStorableAnswer(String key, Class<T> clazz) {
		Object value = answer.get(key);
		if(value != null) {
			 return Memory.deserialize(value, clazz);
		} else {
			return null;
		}
	}
	
}
