package com.p2c.thelife.model;

//import java.util.ArrayList;


public abstract class AbstractDS {
	
	// protected ArrayList<DataStoreListener> m_listeners = new ArrayList<DataStoreListener>();
	protected DataStoreListener m_listener = null;
	
	public void addDataStoreListener(DataStoreListener theListener) {
		//m_listeners.add(theListener);
		m_listener = theListener;
	}
	
	public void notifyDataStoreListeners() {
//		for (DataStoreListener listener:m_listeners) {
//			listener.notifyDataChanged();
//		}
		m_listener.notifyDataChanged();
	}
	
	public void removeDataStoreListener(DataStoreListener theListener) {
		//m_listeners.remove(theListener);
		m_listener = null;
	}
	
	public void clearAllListeners() {
		//m_listeners.clear();
		m_listener = null;
	}

}
