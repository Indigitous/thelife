package com.p2c.thelife;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;



/**
 * Let the user select an image from the device (camera or gallery).
 * @author clarence
 *
 */
public class ImageSelectDialog extends DialogFragment {
	
	public static final int REQUESTCODE_CAMERA = 1;
	public static final int REQUESTCODE_GALLERY = 2;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		
		alertBuilder.setMessage(getResources().getString(R.string.confirm_update_image));
		alertBuilder.setNegativeButton(R.string.cancel, null);	
		alertBuilder.setNeutralButton(R.string.get_camera_photo, new DialogInterface.OnClickListener() {
			
			/**
			 * Use the built in camera app to take a picture.
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				getActivity().startActivityForResult(intent, REQUESTCODE_CAMERA);			
			}
		});
		alertBuilder.setPositiveButton(R.string.get_gallery_photo, new DialogInterface.OnClickListener() {
			
			/**
			 * Use the built in gallery app to get an existing picture.
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				getActivity().startActivityForResult(intent, REQUESTCODE_GALLERY);	
			}
		});
		
		return alertBuilder.create();				
	}

}
