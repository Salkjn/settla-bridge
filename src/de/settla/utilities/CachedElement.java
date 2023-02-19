package de.settla.utilities;

public class CachedElement<T> {
	
	enum DownloadState {
		DOWNLOADED, DOWNLOADING, NOTHING;
	}
	
	private final Object lock = new Object();
	private final long update_intervall;
	private long update_time;
	private T element;
	private DownloadState state;

	public CachedElement(long updateIntervall) {
		super();
		this.update_intervall = updateIntervall;
		this.update_time = 0L;
		this.state = DownloadState.NOTHING;
	}
	
	public boolean isOutdated() {
		synchronized (lock) {
			return System.currentTimeMillis() > update_intervall + update_time; 
		}
	}
	
	public boolean isFresh() {
		synchronized (lock) {
			return System.currentTimeMillis() <= update_intervall + update_time; 
		}
	}
	
	public void startDownload() {
		synchronized (lock) {
			this.state = DownloadState.DOWNLOADING;
		}
	}
	
	public void update(T element) {
		synchronized (lock) {
			this.state = DownloadState.DOWNLOADED;
			this.update_time = System.currentTimeMillis();
			this.element = element;
		}
	}
	
	public boolean isDeleted() {
		synchronized (lock) {
			return isDownloaded() && element == null;
		}
	}
	
	public boolean isDownloaded() {
		synchronized (lock) {
			return state != DownloadState.NOTHING;
		}
	}
	
	public boolean isDownloading() {
		synchronized (lock) {
			return state == DownloadState.DOWNLOADING;
		}
	}
	
	public T get() {
		synchronized (lock) {
			return element;
		}
	}
	
}
