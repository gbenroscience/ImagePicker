package com.itis.libs.imagepick.popups;

import android.content.DialogInterface;
import android.os.Build;
import android.view.View;

import com.itis.libs.imagepick.pages.ScalerActivity;
import com.itis.libs.imagepick.R;

import androidx.appcompat.widget.AppCompatImageView;

public class Options extends TransparentFullScreenDialog {


    AppCompatImageView cameraOption;

    AppCompatImageView galleryOption;




    public Options(final ScalerActivity activity){
        super(activity, R.layout.options);


        cameraOption = findViewById(R.id.camera_option);
        galleryOption = findViewById(R.id.gallery_option);

        cameraOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 23) {
                    activity.takePhotoWithCamera();
                } else {
                    activity.initCameraPermission();
                }
            dismiss();
            }
        });
        galleryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 23) {
                    activity.selectImageFromGallery();
                } else {
                   activity.initGalleryPermission();
                }
                dismiss();
            }
        });


setOnCancelListener(new OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialog) {
        activity.finish();
    }
});

    }



}
