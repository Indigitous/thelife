package com.p2c.thelife.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.p2c.thelife.R;
import com.p2c.thelife.Utilities;


/**
 * Custom view to show a user bitmap, with name on top.
 */
public class UserImageView extends View {
	
	// per instance data; data for the user
	private Bitmap m_bitmap = null;
	private String m_name = null;
	private boolean m_isLeader = false;
	
	// this data does not change per instance
	private static Paint m_paint = null;
	private static float m_bitmapSide = 0f;
	private static float m_gap = 0f;
	private static float m_border = 0f;
	private static float m_textPadLeft = 0f;
	private static float m_nameY = 0f;
	private static RectF m_bitmapRect;
	private static float m_nameTextSize = 0f;
	private static Typeface m_leaderTypeface = null;
	private static Typeface m_memberTypeface = null;
	
	
	/**
	 * Custom view constructor.
	 * @param context
	 * @param attrs
	 */
	public UserImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
			
		// initialize statics before use
		if (m_paint == null) {
			
			// paint
			m_paint = new Paint();
			m_paint.setColor(0xFFFFFFFF);
			m_paint.setAntiAlias(true);
			m_paint.setShadowLayer(3.14f, 3.5f, 3.5f, 0xFF000000);
			
			// precalculate device specific values
			int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
			m_gap = getContext().getResources().getDimension(R.dimen.user_cell_gap);
			m_bitmapSide = (screenWidth - 3 * m_gap) / 2; // half of the width after the left, center and right gaps
			m_textPadLeft = getContext().getResources().getDimension(R.dimen.user_cell_text_padleft);
			m_border = getContext().getResources().getDimension(R.dimen.user_cell_border);
			
			// text specific values
			float m_namePadBottom = getContext().getResources().getDimension(R.dimen.user_cell_name_padbottom);
			m_nameY = m_bitmapSide - m_namePadBottom;
			m_bitmapRect = new RectF(m_border, m_border, m_bitmapSide - m_border, m_bitmapSide - m_border);
			m_nameTextSize = getContext().getResources().getDimension(R.dimen.user_cell_name_textsize);
			
			m_leaderTypeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);
			m_memberTypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
		}		
	}
	
	
	/**
	 * The size of this view is set with fixed values in the XML, but that is only a placeholder.
	 * This routine sets the real size of the view.
	 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)m_bitmapSide, MeasureSpec.EXACTLY);
		int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)m_bitmapSide, MeasureSpec.EXACTLY);
		setMeasuredDimension(childWidthMeasureSpec, childHeightMeasureSpec);
	}
	
	
	/**
	 * Remember the user information of this custom view.
	 * @param bitmap
	 * @param name
	 * @param threshold
	 */
	public void setData(Bitmap bitmap, String name, boolean isLeader) {
		m_bitmap = bitmap;
		m_name = name;
		m_isLeader = isLeader;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		// for development / debugging, allow for missing info
		if (m_bitmap == null) {
			m_bitmap = Utilities.getBitmapFromDrawable(getContext().getResources().getDrawable(R.drawable.generic_avatar_thumbnail));
			m_name = "Friendly Jacksonay";
		}
				
		// Note: nonsquare user bitmaps are stretched to be square
		
		canvas.drawBitmap(m_bitmap, null, m_bitmapRect, m_paint);
		m_paint.setTextSize(m_nameTextSize);
		m_paint.setTypeface(m_isLeader ? m_leaderTypeface : m_memberTypeface);
		canvas.drawText(m_name, m_textPadLeft, m_nameY, m_paint);
	}

}
