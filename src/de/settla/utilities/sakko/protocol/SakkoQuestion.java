package de.settla.utilities.sakko.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("Question")
public class SakkoQuestion extends SakkoConversation {

	private final UUID id;
	private final String type;
	private final Map<String, Object> question;

	public SakkoQuestion(UUID id, String type) {
		this(id, type, new HashMap<>());
	}
	
	public SakkoQuestion(UUID id, String type, Map<String, Object> question) {
		this.id = Objects.requireNonNull(id);
		this.type = Objects.requireNonNull(type);
		this.question = Objects.requireNonNull(question);
	}

	@SuppressWarnings("unchecked")
	public SakkoQuestion(Map<String, Object> map) {
		this.id = StaticParser.parse((String) map.get("id"), UUID.class);
		this.type = (String) map.get("type");
		this.question = (Map<String, Object>) map.get("question");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("id", id.toString());
		map.put("type", type);
		map.put("question", question);
		return map;
	}

	public UUID getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}

	public <T> SakkoQuestion put(String key, T value, Class<T> clazz) {
		question.put(key, StaticParser.unparse(value, clazz));
		return this;
	}
	
	public SakkoQuestion put(String key, Storable data) {
		question.put(key, data == null ? null : data.serialize());
		return this;
	}

	public <T> T getQuestion(String key) {
		Object value = question.get(key);
		if(value != null) {
			 return StaticParser.parse((String)value);
		} else {
			return null;
		}
	}
	
	public <T> T getQuestion(String key, Class<T> clazz) {
		Object value = question.get(key);
		if(value != null) {
			 return StaticParser.parse((String)value, clazz);
		} else {
			return null;
		}
	}
	
	public <T extends Storable> T getStorableQuestion(String key, Class<T> clazz) {
		Object value = question.get(key);
		if(value != null) {
			 return Memory.deserialize(value, clazz);
		} else {
			return null;
		}
	}
	
	public SakkoAnswer answer(Map<String, Object> answer) {
		return new SakkoAnswer(this, answer);
	}
	
	public SakkoAnswer empty() {
		return new EmptyAnswer();
	}
	
	public <T> SakkoAnswer answer() {
		Map<String, Object> map = new HashMap<>();
		return answer(map);
	}
	
}
