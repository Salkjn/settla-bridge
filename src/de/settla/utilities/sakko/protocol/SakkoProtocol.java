package de.settla.utilities.sakko.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.Gson;

import de.settla.utilities.sakko.Sakko;

public class SakkoProtocol {

	public static SakkoProtocol createSakkoProtocol(Sakko sakko) {
		return new SakkoProtocol(sakko);
	}
	
	private static Function<Map<String, Object>, String> convertMapToString = map -> new Gson().toJson(map);
	@SuppressWarnings("unchecked")
	private static Function<String, Map<String, Object>> convertStringToMap = str -> new Gson().fromJson(str, Map.class);
	
	private static Function<String, SakkoConversation> INPUT = str -> {
		if(str.startsWith("Q")) {
			return new SakkoQuestion(convertStringToMap.apply(str.substring(1)));
		} else if(str.startsWith("A")) {
			return new SakkoAnswer(convertStringToMap.apply(str.substring(1)));
		} else {
			return null;
		}
	};
	
	private static Function<SakkoConversation, String> OUTPUT = con -> {
		if(con instanceof SakkoQuestion) {
			return "Q" + convertMapToString.apply(con.serialize());
		} else if(con instanceof SakkoAnswer) {
			return "A" + convertMapToString.apply(con.serialize());
		} else {
			return null;
		}
	};
	
	private final Map<String, Function<SakkoQuestion, SakkoAnswer>> answers = new HashMap<>();
	private final Map<UUID, Conservation> conservations = new HashMap<>();
	private final Object lock = new Object();
	private final Sakko sakko;
	
	private SakkoProtocol(Sakko sakko) {
		this.sakko = sakko;
		sakko.setListener(input -> {
			SakkoConversation conversation = SakkoProtocol.INPUT.apply(input);
			if (conversation == null || conversation instanceof EmptyAnswer)
				return;
			if (conversation instanceof SakkoAnswer) {
				SakkoAnswer answer = (SakkoAnswer) conversation;
				synchronized (lock) {
					Conservation c = conservations.get(answer.getId());
					if(c != null) {
						c.getAnswer().accept(answer);
						conservations.remove(answer.getId());
					}
				}
			} else if (conversation instanceof SakkoQuestion) {
				synchronized (lock) {
					SakkoQuestion question = (SakkoQuestion) conversation;
					Function<SakkoQuestion, SakkoAnswer> f = answers.get(question.getType());
					this.sakko.publish(OUTPUT.apply(f.apply(question)));
				}
			}
		});
	}
	
	private UUID generateId() {
		synchronized (lock) {
			UUID id = null;
			do {
				id = UUID.randomUUID();
			} while (conservations.containsKey(id));
			return id;
		}
	}
	
	public void ask(String type, Function<SakkoQuestion, SakkoQuestion> question, Consumer<SakkoAnswer> answer) {
		synchronized (lock) {
			UUID id = generateId();
			SakkoQuestion q = question.apply(new SakkoQuestion(id, type));
			conservations.put(id, new Conservation(q, answer));
			this.sakko.publish(OUTPUT.apply(q));
		}
	}
	
	public void answer(String type, Function<SakkoQuestion, SakkoAnswer> answer) {
		synchronized (lock) {
			answers.put(type, answer);
		}
	}

}
