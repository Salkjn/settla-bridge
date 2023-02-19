package de.settla.utilities.local;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.lang.RandomStringUtils;

import de.settla.utilities.local.region.Normal;

public class Library<T> {

	private static final int ID_LENGHT = 20;

	private final Map<String, T> map = new HashMap<>();
	private final Object lock = new Object();

	@Nullable
	public T get(String index) {
		checkNotNull(index);
		synchronized (lock) {
			return map.get(index);
		}
	}

	@Nullable
	public T put(String index, @Nullable T element) {
		checkNotNull(index);
		synchronized (lock) {
			return map.put(index, element);
		}
	}

	public int size() {
		synchronized (lock) {
			return map.size();
		}
	}

	public void consume(Consumer<T> consumer) {
		checkNotNull(consumer);
		synchronized (lock) {
			for (T element : map.values()) {
				consumer.accept(element);
			}
		}
	}

	@Nullable
	public T remove(String index) {
		checkNotNull(index);
		synchronized (lock) {
			return map.remove(index);
		}
	}

	public boolean contains(String index) {
		checkNotNull(index);
		synchronized (lock) {
			return map.containsKey(index);
		}
	}

	public boolean containsValue(@Nullable T element) {
		synchronized (lock) {
			return map.containsValue(element);
		}
	}

	/**
	 * Generates a new not used id.
	 * 
	 * @param lenght
	 *            the lenght of the id.
	 * @return a free id.
	 */
	public String generateId() {
		String id = null;
		do {
			id = Normal.normalize(RandomStringUtils.randomAlphabetic(ID_LENGHT));
		} while (contains(id));
		return id;
	}

}
