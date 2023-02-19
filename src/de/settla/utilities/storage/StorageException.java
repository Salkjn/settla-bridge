package de.settla.utilities.storage;

/**
 * Exceptions related to region stores inherit from this exception.
 */
public class StorageException extends Exception {
	
	private static final long serialVersionUID = -1960027554333243146L;

	public StorageException() {
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }

}
