package com.p2c.thelife;

import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/** 
 * Handle the image once the user has chosen.
 * The point of this class is to avoid code duplication.
 * @author clarence
 *
 */
public class ImageSelectSupport {
	
	private static final String TAG = "ImageSelectSupport";
	
	public interface Listener {
		public void notifyImageSelected(Bitmap bitmap);
	}
	
	private Context		   m_context = null;
	private Listener       m_listener = null;
	private ProgressDialog m_progressDialog = null;
	
	
	public ImageSelectSupport(Context context, Listener listener) {
		m_context = context;
		m_listener = listener;
	}
	
	
	/**
	 * Get the result of the photo gathering intent.
	 * See tutorial at http://developer.android.com/training/camera/photobasics.html.
	 * 
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if (resultCode != Activity.RESULT_CANCELED) {
			
			if (requestCode == ImageSelectDialog.REQUESTCODE_CAMERA) {
				
				// just get the low res camera image from the activity result 
				Bundle bundle = intent.getExtras();
				Bitmap bitmap = (Bitmap)bundle.get("data");
				m_listener.notifyImageSelected(bitmap);
				m_context = null;
				m_listener = null;

			} else if (requestCode == ImageSelectDialog.REQUESTCODE_GALLERY) {

				// waiting
				m_progressDialog = ProgressDialog.show(m_context, 
					m_context.getResources().getString(R.string.waiting), m_context.getResources().getString(R.string.processing_image), true, true);

				// activity result is a image content URI
				Uri contentUri = intent.getData();				
				InputStream is = null;
				try {
					// get the bitmap in a background thread
					new GetExternalBitmap().execute(contentUri);
				} catch (Exception e) {
					Log.e(TAG, "onActivityResult()", e);
				} finally {
					if (is != null) {
						try { is.close(); } catch (Exception e) { }
						is = null;
					}
				}
			}
		}
		
	}
	
	/**
	 * Because the external bitmap can be large and internet connections slow, get and process the external bitmap from another thread.
	 * @author clarence
	 *
	 */
	private class GetExternalBitmap extends AsyncTask<Uri, Void, Bitmap> {

		// background thread
		@Override
		protected Bitmap doInBackground(Uri... params) {
			
			InputStream is = null;
			Bitmap bitmap = null;
			try {
				// first pass: just get the size of the image
				is = m_context.getContentResolver().openInputStream(params[0]);								
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, options);
				is.close();
				
				// second pass: get the scaled down version of the image
				is = m_context.getContentResolver().openInputStream(params[0]);
				options.inJustDecodeBounds = false;
				options.inSampleSize = Math.min(options.outHeight / 160, options.outWidth / 160);
				bitmap = BitmapFactory.decodeStream(is, null, options);								
			} catch (Exception e) {
				Log.e(TAG, "GetExternalBitmap()", e);
			} finally {
				if (is != null) {
					try { is.close(); } catch (Exception e) { }
					is = null;
				}
			}
			
			return bitmap;
		}
		
		// UI thread		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			
			// close progress bar
			if (m_progressDialog != null) {
				m_progressDialog.dismiss();
				m_progressDialog = null;
			}				
			
			m_listener.notifyImageSelected(bitmap);
			m_context = null;			
			m_listener = null;
		}		
				
	}	
}
