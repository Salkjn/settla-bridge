package de.settla.utilities.local.region.space.generation;

public interface GenerationListener {

	void changeState(GenerationState from, GenerationState to);
	
	void runningGeneration(GenerationPaster paster);
	
}
