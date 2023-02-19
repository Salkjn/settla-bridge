package de.settla.utilities.sakko;

public interface Sakko extends Runnable {

	SakkoAddress getAddress(); 
	
	void close();
	
	public SakkoListener getListener();

	public void setListener(SakkoListener listener);
	
	public boolean publish(String str);
	
}
