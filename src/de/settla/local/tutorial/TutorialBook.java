package de.settla.local.tutorial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("TutorialBook")
public class TutorialBook implements ConfigurationSerializable {

	private final List<String> pages;
	private final String author;
	private final String title;
	private final String name;

	public TutorialBook(List<String> pages, String author, String title, String name) {
		super();
		this.pages = pages;
		this.author = author;
		this.title = title;
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public TutorialBook(Map<String, Object> map) {
		this.pages = (List<String>) map.get("pages");
		this.author = (String) map.get("author");
		this.title = (String) map.get("title");
		this.name = (String) map.get("name");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pages", pages);
		map.put("author", author);
		map.put("title", title);
		map.put("name", name);
		return map;
	}

	public List<String> getPages() {
		return pages;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

}
