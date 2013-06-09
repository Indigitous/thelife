package com.p2c.thelife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


/**
 * Custom view to show user and friend bitmaps, with an activity/deed icon between.
 */
public class EventUserFriendView extends View {
	
	private Paint m_paint = null;
	private Bitmap m_userBitmap = null;
	private Bitmap m_actionBitmap = null;
	private int    m_actionBitmapWidth = 0;
	private int    m_actionBitmapHeight = 0;
	private Bitmap m_friendBitmap = null;
	private Rect   m_userDestRect = null;
	private Rect   m_friendDestRect = null;
	
	
	public EventUserFriendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// background color is white
		m_paint = new Paint();
		m_paint.setColor(0xFFFFFFFF);
		
		// predeclare objects
		m_userDestRect = new Rect();
		m_friendDestRect = new Rect();		
	}
	
	
	public EventUserFriendView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// background color is white
		m_paint = new Paint();
		m_paint.setColor(0xFFFFFFFF);
		
		// predeclare objects
		m_userDestRect = new Rect();
		m_friendDestRect = new Rect();			
	}
	
	
	public void setBitmaps(Bitmap userBitmap, Bitmap actionBitmap, Bitmap friendBitmap) {
		m_userBitmap = userBitmap;
		m_actionBitmap = actionBitmap;
		m_actionBitmapWidth = actionBitmap.getWidth();
		m_actionBitmapHeight = actionBitmap.getHeight();
		m_friendBitmap = friendBitmap;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// assume a rectangle, width = 2 * height + gap
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		int gap = viewWidth - 2 * viewHeight;
		if (gap < 0) {
			gap = 0;
		}

		// for development / debugging, allow for missing bitmaps
		if (m_userBitmap == null) {
			m_userBitmap = Utilities.getBitmapFromDrawable(getContext().getResources().getDrawable(R.drawable.generic_avatar_thumbnail));
			m_actionBitmap = Utilities.getBitmapFromDrawable(getContext().getResources().getDrawable(R.drawable.activity_move));
			m_actionBitmapWidth = m_actionBitmap.getWidth();
			m_actionBitmapHeight = m_actionBitmap.getHeight();			
			m_friendBitmap = Utilities.getBitmapFromDrawable(getContext().getResources().getDrawable(R.drawable.generic_avatar_thumbnail));
		}
		
		// draw the user on the left side, friend on the right side
		// TODO nonsquare user/friend bitmaps are stretched to be square
		Rect srcRect = null;
		m_userDestRect.set(0,  0, viewHeight, viewHeight);
		canvas.drawBitmap(m_userBitmap, srcRect, m_userDestRect, m_paint);
		m_friendDestRect.set(viewHeight + gap,  0, viewWidth, viewHeight);		
		canvas.drawBitmap(m_friendBitmap, srcRect, m_friendDestRect, m_paint);
				
		// draw the action icon between the user and the friend
		canvas.drawCircle(viewWidth / 2, viewHeight / 2, m_actionBitmapWidth, m_paint);		
		canvas.drawBitmap(m_actionBitmap, (viewWidth - m_actionBitmapWidth) / 2, (viewHeight - m_actionBitmapHeight) / 2, m_paint);
	}

}
