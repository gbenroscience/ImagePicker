package com.itis.libs.imagepick.utils;

import android.net.Uri;

import java.io.File;

public interface ImagePickedListener {

     int ERROR_BITMAP_NOT_LOADED = 1;
     int ERROR_LISTENER_NOT_SET = 2;

    /**
     *
     * @param uri The uri of the {@link Uri}
     * @param file The file that references the image on the device.
     */
    void onImagePicked(Uri uri, File file);

    /**
     *
     * @param errorCode The error code can be one of
     *                  {@link ImagePickedListener#ERROR_BITMAP_NOT_LOADED}
     *                  OR
     *                  {@link ImagePickedListener#ERROR_LISTENER_NOT_SET}
     */
     void onPickerError(int errorCode);

}
