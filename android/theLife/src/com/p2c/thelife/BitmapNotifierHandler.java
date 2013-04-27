package com.p2c.thelife;

import java.io.File;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


/**
 * 
 * @author clarence
 *
 */
public class BitmapNotifierHandler extends Handler {
	
	private static final String TAG = "BitmapNotifierHandler";
	
	private static final int OP_USER = 1;
	private static final int OP_FRIEND = 2;	
	private static final int OP_ACTIVITY = 3;
	
	
	/**
	 * Listener interface for Bitmap-is-now-available occurrences.
	 *
	 */
	public interface UserBitmapListener {
		public void notifyUserBitmap(int id);
	}
	
	public interface FriendBitmapListener {
		public void notifyFriendBitmap(int id);
	}
	
	public interface ActivityBitmapListener {
		public void notifyActivityBitmap(int id);
	}	
	
	
	
	// start the Handler on the UI thread
	public BitmapNotifierHandler() {
		super(Looper.getMainLooper());
	}
	
	@Override
	public void handleMessage(Message message) {
System.out.println("BitmapNotifierHandler got message " + message);

		// parse the message
		int op = 0;
		switch (message.what) {
			case BitmapCacheHandler.OP_GET_USER_IMAGE_FROM_SERVER:
			case BitmapCacheHandler.OP_GET_USER_THUMBNAIL_FROM_SERVER:
				op = OP_USER;
				break;
			case BitmapCacheHandler.OP_GET_FRIEND_IMAGE_FROM_SERVER:
			case BitmapCacheHandler.OP_GET_FRIEND_THUMBNAIL_FROM_SERVER:
				op = OP_FRIEND;
				break;		
			case BitmapCacheHandler.OP_GET_ACTIVITY_IMAGE_FROM_SERVER:
			case BitmapCacheHandler.OP_GET_ACTIVITY_THUMBNAIL_FROM_SERVER:
				op = OP_ACTIVITY;
				break;
			default:
				Log.e(TAG, "Bad message in BitmapNotifierHandler " + message.what);
				return;
		}
		int id = message.arg1;

		// rename the temporary cache file, which was just written by the BitmapCacheHandler, to the cache file
		String temporaryCacheFileName = (String)message.obj;
		File temporaryFile = new File(temporaryCacheFileName);
		File cacheFile = new File(temporaryCacheFileName.substring(0, temporaryCacheFileName.length() - 1)); // just drop the last character in the name
		temporaryFile.renameTo(cacheFile);
		
		// notify all the listeners
		if (op == OP_USER) {
			notifyUserBitmapListeners(id);
		} else if (op == OP_FRIEND) {
			notifyFriendBitmapListeners(id);
		} else if (op == OP_ACTIVITY) {
			notifyActivityBitmapListeners(id);
		}
	}
	
	
	/************************************* Listeners *****************************************/
	
	/**
	 * UserBitmap listeners
	 */
	protected ArrayList<UserBitmapListener> m_userBitmapListeners = new ArrayList<UserBitmapListener>();
	
	public void addUserBitmapListener(UserBitmapListener theListener) {
		m_userBitmapListeners.add(theListener);
	}
	
	public void notifyUserBitmapListeners(int id) {
		for (UserBitmapListener listener:m_userBitmapListeners) {
			if (listener != null) {
				listener.notifyUserBitmap(id);
			}
		}
	}
	
	public void removeUserBitmapListener(UserBitmapListener theListener) {
		m_userBitmapListeners.remove(theListener);
	}
	
	public void clearAllUserBitmapListeners() {
		m_userBitmapListeners.clear();
	}	
	

	/**
	 * FriendBitmap listeners
	 */
	protected ArrayList<FriendBitmapListener> m_FriendBitmapListeners = new ArrayList<FriendBitmapListener>();
	
	public void addFriendBitmapListener(FriendBitmapListener theListener) {
		m_FriendBitmapListeners.add(theListener);
	}
	
	public void notifyFriendBitmapListeners(int id) {
		for (FriendBitmapListener listener:m_FriendBitmapListeners) {
			if (listener != null) {
				listener.notifyFriendBitmap(id);
			}
		}
	}
	
	public void removeFriendBitmapListener(FriendBitmapListener theListener) {
		m_FriendBitmapListeners.remove(theListener);
	}
	
	public void clearAllFriendBitmapListeners() {
		m_FriendBitmapListeners.clear();
	}	
		
	
	
	/**
	 * ActivityBitmap listeners
	 */
	protected ArrayList<ActivityBitmapListener> m_ActivityBitmapListeners = new ArrayList<ActivityBitmapListener>();
	
	public void addActivityBitmapListener(ActivityBitmapListener theListener) {
		m_ActivityBitmapListeners.add(theListener);
	}
	
	public void notifyActivityBitmapListeners(int id) {
		for (ActivityBitmapListener listener:m_ActivityBitmapListeners) {
			if (listener != null) {
				listener.notifyActivityBitmap(id);
			}
		}
	}
	
	public void removeActivityBitmapListener(ActivityBitmapListener theListener) {
		m_ActivityBitmapListeners.remove(theListener);
	}
	
	public void clearAllActivityBitmapListeners() {
		m_ActivityBitmapListeners.clear();
	}	
	
}
