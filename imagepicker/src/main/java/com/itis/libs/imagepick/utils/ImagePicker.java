package com.itis.libs.imagepick.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.itis.libs.imagepick.pages.ScalerActivity;

import java.io.File;

public class ImagePicker {


    /**
     * Request code sent by the user
     */
    private int requestCode;

    public static final int OPEN_GALLERY = 1;
    public static final int OPEN_CAMERA = 2;
    public static final int OPEN_CHOOSER = 3;


    public static final String KEY_REQUEST_CODE = "requestCode";


    public static final String KEY_HEADER_COLOR = "header_color_key";
    public static final String KEY_BG_COLOR = "bg_color_key";
    public static final String KEY_CROPPER_COLOR = "cropper_color_key";
    public static final String KEY_SHOW_PREVIEW = "show_preview_key";
    public static final String KEY_CROP_NEEDED = "crop_needed_key";
    public static final String KEY_SHOW_GRID = "show_grid_key";
    public static final String KEY_CROP_THICKNESS = "crop_thickness_key";


    public static final String RESULT_FILE_PATH = "result_path";


    private ImagePickedListener imagePickedListener;

    private Props props;


    private ImagePicker() {
        props = new Props();
    }


    public static final ImagePicker createDefaultPicker() {
        ImagePicker imagePicker = new ImagePicker();
        imagePicker.setCropperTheme("#16813B", "#000000", "#ffffff", true, true, true,4);
        return imagePicker;
    }


    public static final ImagePicker createPicker() {
        return createDefaultPicker();
    }


    /**
     * @param headerColor        The hexadecimal representation of the header color
     * @param bgColor            The hexadecimal representation of the background color
     * @param cropperBorderColor The hexadecimal representation of the cropper square's border color
     * @param showPreview        If true, will show the live preview of the selected area
     * @param needsCrop          The image needs to be cropped after being picked.
     * @param showGrid           If true, show the grid lines in the cropper.
     * @param cropRectThickness      The thickness of the rectangle used to select the cropping area.
     * @return the picker.
     */
    private ImagePicker setCropperTheme(String headerColor, String bgColor, String cropperBorderColor, boolean showPreview, boolean needsCrop, boolean showGrid, int cropRectThickness) {
        props.setBgColor(bgColor);
        props.setHeaderColor(headerColor);
        props.setCropperBorderColor(cropperBorderColor);
        props.setShowPreview(showPreview);
        props.setNeedsCrop(needsCrop);
        props.setShowGrid(showGrid);
        props.setCropRectThickness(cropRectThickness);
        return this;
    }

    /**
     * @param bgColor The hexadecimal representation of the background color of the cropper
     * @return the picker.
     */
    public ImagePicker bgColor(String bgColor) {
        props.setBgColor(bgColor);
        return this;
    }

    /**
     * @param headerColor The hexadecimal representation of the header color of the cropper
     * @return the picker.
     */
    public ImagePicker headerColor(String headerColor) {
        props.setHeaderColor(headerColor);
        return this;
    }

    /**
     * @param cropperBorderColor The hexadecimal representation of the border color of the cropper
     * @return the picker.
     */
    public ImagePicker cropperBorderColor(String cropperBorderColor) {
        props.setCropperBorderColor(cropperBorderColor);
        return this;
    }


    /**
     * @param showPreview If true, will show the live preview of the cropped image in a separate square
     * @return the picker.
     */
    public ImagePicker showPreview(boolean showPreview) {
        props.setShowPreview(showPreview);
        return this;
    }

    /**
     * @param showGrid If true, will show the grid lines in the cropper square.
     * @return the picker.
     */
    public ImagePicker showGrid(boolean showGrid) {
        props.setShowGrid(showGrid);
        return this;
    }


    /**
     * @param needsCrop If true, the image picked needs to be cropped.
     * @return the picker.
     */
    public ImagePicker needsCrop(boolean needsCrop) {
        props.setNeedsCrop(needsCrop);
        return this;
    }

    /**
     * @param cropRectThickness The thickness of the crop area's sides and its grid, if any.
     * @return the picker.
     */
    public ImagePicker cropRectThickness(int cropRectThickness) {
        props.setCropRectThickness(cropRectThickness);
        return this;
    }

    private void loadProps(Intent intent, int requestCode) {
        intent.putExtra(KEY_REQUEST_CODE, requestCode)
        .putExtra(KEY_CROP_NEEDED, props.isNeedsCrop())
        .putExtra(KEY_HEADER_COLOR, props.getHeaderColor())
        .putExtra(KEY_BG_COLOR, props.getBgColor())
        .putExtra(KEY_CROPPER_COLOR, props.getCropperBorderColor())
        .putExtra(KEY_SHOW_PREVIEW, props.isShowPreview())
        .putExtra(KEY_SHOW_GRID, props.isShowGrid())
        .putExtra(KEY_CROP_THICKNESS, props.getCropRectThickness());
    }

    /**
     * @param client The activity that needs to call the {@link ImagePicker}
     * @return
     */
    public ImagePicker openChooser(Activity client) {
        Intent startIntent = new Intent(client, ScalerActivity.class);

        requestCode = OPEN_CHOOSER;
        loadProps(startIntent, requestCode);

        client.startActivityForResult(startIntent, requestCode);

        return this;
    }


    public ImagePicker openCamera(Activity client) {
        Intent startIntent = new Intent(client, ScalerActivity.class);

        requestCode = OPEN_CAMERA;

        loadProps(startIntent, requestCode);

        client.startActivityForResult(startIntent, requestCode);

        return this;
    }


    public ImagePicker openGallery(Activity client) {
        Intent startIntent = new Intent(client, ScalerActivity.class);

        requestCode = OPEN_GALLERY;
        loadProps(startIntent, requestCode);

        client.startActivityForResult(startIntent, requestCode);


        return this;
    }



    public void handleActivityResult(int requestCode, int resultCode, Intent data, Activity orginalCaller) {
        Log.d(Utils.class.getName(), "handle-activity called" );
        if (requestCode == this.requestCode) {
            Log.d(Utils.class.getName(), "Seen requestcode." );
            if (resultCode == Activity.RESULT_OK) {
                if (imagePickedListener != null) {
                    Uri uri = data.getData();
                    String path = data.getStringExtra(RESULT_FILE_PATH);
                    File imageFile = new File(path);

                    Log.d(Utils.class.getName(), "file-path>>>>>: "+path );
                    Log.d(Utils.class.getName(), "uri-path>>>>>: "+ uri.getPath() );


                    imagePickedListener.onImagePicked(uri,imageFile);
                } else {
                    Utils.showShortToast(orginalCaller, "Please set the ImagePickedListener");
                }
            } else {
                imagePickedListener.onPickerError(ImagePickedListener.ERROR_BITMAP_NOT_LOADED);
            }
        }
    }

    public void setImagePickedListener(ImagePickedListener imagePickedListener) {
        this.imagePickedListener = imagePickedListener;
    }

    public ImagePickedListener getImagePickedListener() {
        return imagePickedListener;
    }

}
