package com.p2c.thelife.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.p2c.thelife.R;
import com.p2c.thelife.Utilities;
import com.p2c.thelife.model.FriendModel;


/**
 * Custom view to show a friend bitmap, with name and threshold on top.
 */
public class FriendImageView extends View {
	
	// per instance data; data for the friend
	private Bitmap m_bitmap = null;
	private String m_name = null;
	private String m_threshold = null;
	
	// this data does not change per instance
	private static Paint m_paint = null;
	private static float m_bitmapSide = 0f;
	private static float m_gap = 0f;
	private static float m_border = 0f;
	private static float m_textPadLeft = 0f;
	private static float m_nameY = 0f;
	private static float m_thresholdY = 0f;
	private static RectF m_bitmapRect;
	private static float m_nameTextSize = 0f;
	private static float m_thresholdTextSize = 0f;
	
	
	/**
	 * Custom view constructor.
	 * @param context
	 * @param attrs
	 */
	public FriendImageView(Context context, AttributeSet attrs) {
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
			m_gap = getContext().getResources().getDimension(R.dimen.friend_cell_gap);
			m_bitmapSide = (screenWidth - 3 * m_gap) / 2; // half of the width after the left, center and right gaps
			m_textPadLeft = getContext().getResources().getDimension(R.dimen.friend_cell_text_padleft);
			m_border = getContext().getResources().getDimension(R.dimen.friend_cell_border);
			
			// text specific values
			float m_namePadBottom = getContext().getResources().getDimension(R.dimen.friend_cell_name_padbottom);
			float m_thresholdPadBottom = getContext().getResources().getDimension(R.dimen.friend_cell_threshold_padbottom);
			m_nameY = m_bitmapSide - m_thresholdPadBottom - m_namePadBottom;
			m_thresholdY = m_bitmapSide - m_thresholdPadBottom;
			m_bitmapRect = new RectF(m_border, m_border, m_bitmapSide - m_border, m_bitmapSide - m_border);
			m_nameTextSize = getContext().getResources().getDimension(R.dimen.friend_cell_name_textsize);
			m_thresholdTextSize = getContext().getResources().getDimension(R.dimen.friend_cell_threshold_textsize);
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
	 * Remember the friend information of this custom view.
	 * @param bitmap
	 * @param name
	 * @param threshold
	 */
	public void setData(Bitmap bitmap, String name, String threshold) {
		m_bitmap = bitmap;
		m_name = name;
		m_threshold = threshold;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		// for development / debugging, allow for missing info
		if (m_bitmap == null) {
			m_bitmap = Utilities.getBitmapFromDrawable(getContext().getResources().getDrawable(R.drawable.generic_avatar_thumbnail));
			m_name = "Friendly Jacksonay";
			m_threshold = FriendModel.getThresholdMediumString(getContext().getResources(), FriendModel.Threshold.Open);
		}
				
		// Note: nonsquare friend bitmaps are stretched to be square
		
		canvas.drawBitmap(m_bitmap, null, m_bitmapRect, m_paint);
		m_paint.setTextSize(m_nameTextSize);
		canvas.drawText(m_name, m_textPadLeft, m_nameY, m_paint);
		m_paint.setTextSize(m_thresholdTextSize);
		canvas.drawText(m_threshold, m_textPadLeft, m_thresholdY, m_paint);		
	}

}
