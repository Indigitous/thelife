package com.p2c.thelife;

import java.util.ArrayList;

import com.p2c.thelife.model.DataStoreListener;

public abstract class AbstractDS {
	
	protected ArrayList<DataStoreListener> m_listeners = new ArrayList<DataStoreListener>();
	
	public void addDataStoreListener(DataStoreListener theListener) {
		m_listeners.add(theListener);
	}
	
	public void notifyDataStoreListeners() {
		for (DataStoreListener listener:m_listeners) {
			listener.notifyDataChanged();
		}
	}
	
	public void removeDataStoreListener(DataStoreListener theListener) {
		m_listeners.remove(theListener);
	}
	
	public void clearAllListeners() {
		m_listeners.clear();
	}

}
