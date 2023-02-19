package de.settla.utilities.sakko.protocol;

import java.util.function.Consumer;

public class Conservation {
	
	private final SakkoQuestion question;
	private final Consumer<SakkoAnswer> answer;
	
	public Conservation(SakkoQuestion question, Consumer<SakkoAnswer> answer) {
		this.question = question;
		this.answer = answer;
	}
	
	public SakkoQuestion getQuestion() {
		return question;
	}

	public Consumer<SakkoAnswer> getAnswer() {
		return answer;
	}
	
}
