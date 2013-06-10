package com.p2c.thelife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
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
	private static float m_radia[] = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };
	private static RoundRectShape m_roundRectShape = null;
	private static ShapeDrawable m_roundCornersShape = null;
	private static float m_bitmapSide = 0f;
	private static float m_bitmapsLength = 0f;
	
	
	public EventUserFriendView(Context context, AttributeSet attrs) {
		super(context, attrs);
			
		// initialize statics before use
		if (m_paint == null) {
			
			// paint
			m_paint = new Paint();
			m_paint.setColor(context.getResources().getColor(R.color.event_background));
			
			// bitmap rectangles
			m_bitmapSide = (int)context.getResources().getDimension(R.dimen.thumbnail_side);
			int gap = (int)context.getResources().getDimension(R.dimen.event_gap_between_bitmaps);
			m_bitmapsLength = (int)context.getResources().getDimension(R.dimen.event_bitmaps_length);
			m_userDestRect = new Rect(0, 0, (int)m_bitmapSide, (int)m_bitmapSide);
			m_friendDestRect = new Rect((int)m_bitmapSide + gap,  0, (int)m_bitmapsLength, (int)m_bitmapSide);
			
			// rounded corners
			for (int i = 0; i < m_radia.length; i++) {
				m_radia[i] = context.getResources().getDimension(R.dimen.event_radius);
			}
			m_roundRectShape = new RoundRectShape(m_radia, null, null);
			m_roundCornersShape = new ShapeDrawable(m_roundRectShape);
			m_roundCornersShape.setColorFilter(context.getResources().getColor(R.color.event_background), Mode.XOR);			
		}		
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
		m_roundCornersShape.setBounds(m_userDestRect);
		m_roundCornersShape.draw(canvas);
		m_roundCornersShape.setBounds(m_friendDestRect);		
		m_roundCornersShape.draw(canvas);		
		
		// draw the action icon between the user and the friend
		canvas.drawCircle(m_bitmapsLength / 2, m_bitmapSide / 2, m_actionBitmapWidth, m_paint);		
		canvas.drawBitmap(m_actionBitmap, (m_bitmapsLength - m_actionBitmapWidth) / 2, (m_bitmapSide - m_actionBitmapHeight) / 2, m_paint);
	}

}
