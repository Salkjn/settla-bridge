package de.settla.utilities.storage;

public class Storage<T extends Storable> implements Runnable {
	
	private final Database<T> database;
	private T object;
	private final Object lock = new Object();
	
	public Storage(Database<T> database) {
		super();
		this.database = database;
	}

	public T object() {
		return object;
	}

	@Override
	public void run() {
		synchronized (lock) {
			if(object == null) {
				try {
					object = database.load();
				} catch (StorageException e) {
					e.printStackTrace();
				}
				System.out.println("STORAGE: LOADED:" + (object != null) + " " + object.toString());
			} else {
				if(object.isDirty()) {
					try {
						database.save(object);
						object.setDirty(false);
					} catch (StorageException e) {
						e.printStackTrace();
					}
					System.out.println("STORAGE: UPDATED:" + (object != null) + " " + object.toString());
				}
			}
		}
	}
}
