package com.p2c.thelife.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.p2c.thelife.R;
import com.p2c.thelife.Utilities;


/**
 * Custom view to show the square image with rounded corners.
 */
public class RoundedImageView extends View {
	
	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	private Bitmap m_bitmap = null;
	private Paint m_paint = null;
	private Rect m_imageDestRect = null;
	private static Bitmap m_roundRectBitmap = null;

	
	/**
	 * Set the bitmap and related info of this custom view.
	 * Works best if the bitmap is square.
	 * @param bitmap
	 * @param backgroundColor
	 * @param sideLength
	 * @param radius			the rounding radius (dimension)
	 */
	public void setImageInfo(Bitmap bitmap, int backgroundColor, float sideLength, float radius) {
		m_bitmap = bitmap;
		int sideLengthInt = (int)sideLength;
		
		// paint
		m_paint = new Paint();
		m_paint.setColor(backgroundColor);
		m_paint.setAntiAlias(true);
		
		// bitmap rectangles
		m_imageDestRect = new Rect(0, 0, sideLengthInt, sideLengthInt);
				
		// create a bitmap that will round the corners of the bitmap
		
		// create the bitmap with the same size as the bitmap, and make it the background color
		m_roundRectBitmap = Bitmap.createBitmap(sideLengthInt, sideLengthInt, Bitmap.Config.ARGB_8888);
		for (int x = 0; x < sideLength; x++) {
			for (int y = 0; y < sideLength; y++) {
				m_roundRectBitmap.setPixel(x, y, backgroundColor);
			}
		}
		
		// draw the rounded edges onto the bitmap
		Paint roundedEdgePaint = new Paint();
		int tempColor = 0xFF808080;
		roundedEdgePaint.setColor(0xFF808080); // any solid color that is not the event_background
		Canvas canvas = new Canvas(m_roundRectBitmap);
		RectF rect = new RectF(0f, 0f, sideLength, sideLength);
		canvas.drawRoundRect(rect, radius, radius, roundedEdgePaint);
		
		// and make the inside of the bitmap transparent
		for (int x = 0; x < sideLengthInt; x++) {
			for (int y = 0; y < sideLengthInt; y++) {
				if (m_roundRectBitmap.getPixel(x, y) == tempColor) {
					m_roundRectBitmap.setPixel(x, y, 0); // transparent color
				}
			}
		}					
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		// for development / debugging, allow dummy data
		if (m_bitmap == null) {
			m_bitmap = Utilities.getBitmapFromDrawable(getContext().getResources().getDrawable(R.drawable.generic_avatar_thumbnail));
			m_roundRectBitmap = m_bitmap;
			m_paint = new Paint();
			m_imageDestRect = new Rect(0, 0, getWidth(), getHeight());
		}
				
		// draw the image bitmap
		// TODO nonsquare bitmaps are stretched to be square
		canvas.drawBitmap(m_bitmap, null, m_imageDestRect, m_paint);
		
		// round the bitmap corners
		canvas.drawBitmap(m_roundRectBitmap, null, m_imageDestRect, m_paint);
	}

}
