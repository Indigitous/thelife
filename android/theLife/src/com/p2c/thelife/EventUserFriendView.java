package com.p2c.thelife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Custom view to show user and friend bitmaps, with an activity/deed icon between.
 */
public class EventUserFriendView extends View {
	
	// per instance data
	private Bitmap m_userBitmap = null;
	private Bitmap m_actionBitmap = null;
	private int    m_actionBitmapWidth = 0;
	private int    m_actionBitmapHeight = 0;
	private Bitmap m_friendBitmap = null;
	
	// this data does not change per instance
	private static Paint m_paint = null;
	private static Rect m_userDestRect = null;
	private static Rect m_friendDestRect = null;
	private static float m_bitmapSide = 0f;
	private static float m_bitmapsLength = 0f;
	private static Bitmap m_roundRectBitmap = null;
	
	
	/**
	 * Custom view constructor.
	 * @param context
	 * @param attrs
	 */
	public EventUserFriendView(Context context, AttributeSet attrs) {
		super(context, attrs);
			
		// initialize statics before use
		if (m_paint == null) {
			
			// paint
			m_paint = new Paint();
			m_paint.setColor(context.getResources().getColor(R.color.event_background));
			m_paint.setAntiAlias(true);
			
			// bitmap rectangles
			m_bitmapSide = (int)context.getResources().getDimension(R.dimen.thumbnail_side);
			int gap = (int)context.getResources().getDimension(R.dimen.event_gap_between_bitmaps);
			m_bitmapsLength = (int)context.getResources().getDimension(R.dimen.event_bitmaps_length);
			m_userDestRect = new Rect(0, 0, (int)m_bitmapSide, (int)m_bitmapSide);
			m_friendDestRect = new Rect((int)m_bitmapSide + gap,  0, (int)m_bitmapsLength, (int)m_bitmapSide);
					
			// create a bitmap that will round the corners of the user and friend bitmaps
			
			// create the bitmap with the same size as the user and friend bitmaps, and make it the background color
			int sidePixels = (int)m_bitmapSide;
			m_roundRectBitmap = Bitmap.createBitmap(sidePixels, sidePixels, Bitmap.Config.ARGB_8888);
			for (int x = 0; x < sidePixels; x++) {
				for (int y = 0; y < sidePixels; y++) {
					m_roundRectBitmap.setPixel(x, y, context.getResources().getColor(R.color.event_background));
				}
			}
			
			// draw the rounded edges onto the bitmap
			Paint roundedEdgePaint = new Paint();
			int tempColor = 0xFF808080;
			roundedEdgePaint.setColor(0xFF808080); // any solid color that is not the event_background
			Canvas canvas = new Canvas(m_roundRectBitmap);
			RectF rect = new RectF(0f, 0f, (float)sidePixels, (float)sidePixels);
			float eventRadius = context.getResources().getDimension(R.dimen.event_radius);
			canvas.drawRoundRect(rect, eventRadius, eventRadius, roundedEdgePaint);
			
			// and make the inside of the bitmap transparent
			for (int x = 0; x < sidePixels; x++) {
				for (int y = 0; y < sidePixels; y++) {
					if (m_roundRectBitmap.getPixel(x, y) == tempColor) {
						m_roundRectBitmap.setPixel(x, y, 0); // transparent color
					}
				}
			}			
			
		}		
	}
	
	
	/**
	 * Remember the bitmaps of this custom view.
	 * @param userBitmap
	 * @param actionBitmap
	 * @param friendBitmap
	 */
	public void setBitmaps(Bitmap userBitmap, Bitmap actionBitmap, Bitmap friendBitmap) {
		m_userBitmap = userBitmap;
		m_actionBitmap = actionBitmap;
		m_actionBitmapWidth = actionBitmap.getWidth();
		m_actionBitmapHeight = actionBitmap.getHeight();
		m_friendBitmap = friendBitmap;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

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
		canvas.drawBitmap(m_userBitmap, null, m_userDestRect, m_paint);
		canvas.drawBitmap(m_friendBitmap, null, m_friendDestRect, m_paint);
		
		// round the bitmap corners
		canvas.drawBitmap(m_roundRectBitmap, null, m_userDestRect, m_paint);
		canvas.drawBitmap(m_roundRectBitmap, null, m_friendDestRect, m_paint);		
		
		// draw the action icon between the user and the friend
		canvas.drawCircle(m_bitmapsLength / 2, m_bitmapSide / 2, m_actionBitmapWidth, m_paint);		
		canvas.drawBitmap(m_actionBitmap, (m_bitmapsLength - m_actionBitmapWidth) / 2, (m_bitmapSide - m_actionBitmapHeight) / 2, m_paint);
	}

}
