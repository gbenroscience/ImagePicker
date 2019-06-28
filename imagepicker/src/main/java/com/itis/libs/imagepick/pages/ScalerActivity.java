package com.itis.libs.imagepick.pages;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.itis.libs.imagepick.utils.Conf;
import com.itis.libs.imagepick.utils.Props;
import com.itis.libs.imagepick.utils.Utils;
import com.itis.libs.imagepick.R;
import com.itis.libs.imagepick.popups.Options;
import com.itis.libs.imagepick.utils.ImagePicker;
import com.itis.libs.imagepick.utils.ImageUtilities;
import com.itis.libs.imagepick.views.ImageScalant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;


public class ScalerActivity extends AppCompatActivity {

    public static ImageScalant scalant;
    private Uri currentUri;
    File croppersFolder;
    //private static String selectedImagePath;


    public static final int SELECT_PICTURE_FROM_GALLERY = ImagePicker.OPEN_GALLERY;
    public static final int SELECT_PICTURE_FROM_CAMERA = ImagePicker.OPEN_CAMERA;


    private AppCompatImageView rotateButton;
    AppCompatButton cropBtn;
    AppCompatImageView backBtn;

    private View header;


    private int requestCode;

    private boolean isOldVersion = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;

    Props props;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        setContentView(R.layout.scaler_view);

        init();
        receiveProps(getIntent());


        createStorageDir();

        Log.d(ScalerActivity.class.getName() , "Restarted ScalerActivity<<<--->>><<<--->>>");
        /**
         * Purge past crops
         */
        for (File f : croppersFolder.listFiles()) {
            f.delete();
        }


        createStorageDir();


        if (this.requestCode == -1) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }

        switch (requestCode) {

            case ImagePicker.OPEN_CAMERA:

                if (Build.VERSION.SDK_INT < 23) {
                    takePhotoWithCamera();
                } else {
                    initCameraPermission();
                }

                break;
            case ImagePicker.OPEN_GALLERY:
                if (Build.VERSION.SDK_INT < 23) {
                    selectImageFromGallery();
                } else {
                    initGalleryPermission();
                }

                break;
            case ImagePicker.OPEN_CHOOSER:
                showHeader(false);
                selectImage(this);

                break;
                default:
                    break;

        }


        rotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (scalant != null && scalant.hasImage()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = scalant.getImage();
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    scalant.setImage(rotatedBitmap);
                } else {
                    Utils.showShortToast(ScalerActivity.this, "Please set an image");
                }
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        cropBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(ScalerActivity.class.getName() , "Crop---1");
                File f = createImageFile();
                Log.d(ScalerActivity.class.getName() , "Crop---2---> File will go to: "+f.getAbsolutePath());
                String path = f.getAbsolutePath();
                if (scalant.getCapturedBitmap() != null) {
                    Log.d(ScalerActivity.class.getName() , "Crop---3---> Bitmap found");
                    try {
                        ImageUtilities.saveImage(scalant.getCapturedBitmap(), path);
                        Intent data = new Intent();
                        data.setData(Utils.getUri(f, ScalerActivity.this.getApplicationContext()));
                        data.putExtra(ImagePicker.RESULT_FILE_PATH, path);
                        Log.d(ScalerActivity.class.getName() , "Crop---4--> Path saved to Intent");
                        setResult(RESULT_OK, data);
                        Log.d(ScalerActivity.class.getName() , "Crop---5--> Path saved to Intent");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Intent data = new Intent();
                        ImageUtilities.saveImage(Bitmap.createBitmap(10, 10, Config.ARGB_8888), path);
                        data.setData(Utils.getUri(f, ScalerActivity.this.getApplicationContext()));
                        data.putExtra(ImagePicker.RESULT_FILE_PATH, path);
                        setResult(RESULT_CANCELED, data);
                        Utils.showShortToast(ScalerActivity.this, "Couldn't get image! Please try again!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finish();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(Utils.class.getName(), "ScalerActivity resumes" );
    }

    private void init() {


        scalant = findViewById(R.id.crop_view);
        cropBtn = findViewById(R.id.crop_image);
        rotateButton = findViewById(R.id.rotate_image);
        header = findViewById(R.id.header);
        backBtn = findViewById(R.id.back_btn);

        props = new Props();

    }

    public void showHeader(boolean show){
if(show){
    header.setVisibility(View.VISIBLE);
    cropBtn.setVisibility(View.VISIBLE);
    rotateButton.setVisibility(View.VISIBLE);
    backBtn.setVisibility(View.VISIBLE);

}else{
    header.setVisibility(View.GONE);
    cropBtn.setVisibility(View.GONE);
    rotateButton.setVisibility(View.GONE);
    backBtn.setVisibility(View.GONE);
}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.unbindDrawables(scalant);
        scalant = null;
    }

    /**
     * @param activity The activity that needs the image
     *                 Used to select the image from the gallery or the camera.
     */
    public void selectImage(final ScalerActivity activity) {
        Options options = new Options(activity);
        options.show();
    }

    private void receiveProps(Intent intent) {

        requestCode = intent.getIntExtra(ImagePicker.KEY_REQUEST_CODE, requestCode);
        props.setBgColor(intent.getIntExtra(ImagePicker.KEY_BG_COLOR, Color.WHITE));
        props.setHeaderColor(intent.getIntExtra(ImagePicker.KEY_HEADER_COLOR, Color.WHITE));
        props.setCropperBorderColor(intent.getIntExtra(ImagePicker.KEY_CROPPER_COLOR, Color.WHITE));
        props.setNeedsCrop(intent.getBooleanExtra(ImagePicker.KEY_CROP_NEEDED, false));
        props.setShowPreview(intent.getBooleanExtra(ImagePicker.KEY_SHOW_PREVIEW, true));
        props.setShowGrid(intent.getBooleanExtra(ImagePicker.KEY_SHOW_GRID, true));
        props.setCropRectThickness(intent.getIntExtra(ImagePicker.KEY_CROP_THICKNESS, 3));

    }

    private void loadProps() {
        header.setBackgroundColor(props.getHeaderColor());
        scalant.setBackgroundColor(props.getBgColor());
        scalant.setShowPreview(props.isShowPreview());
        scalant.setCropperBorderColor(props.getCropperBorderColor());
        scalant.setShowGrid(props.isShowGrid());
        scalant.setCropThickness(props.getCropRectThickness());
    }


    public void takePhotoWithCamera() {

        requestCode = SELECT_PICTURE_FROM_CAMERA;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();//Utils.TEMP_IMAGE_FILE;//
            } catch (Exception ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Utils.TEMP_IMAGE_FILE = photoFile;

                Context appContext = getApplicationContext();

                currentUri = FileProvider.getUriForFile(appContext, appContext.getPackageName()+ Utils.AUTHORITY_SUFFIX, Utils.TEMP_IMAGE_FILE);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentUri);
                if (!isOldVersion) {
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, currentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                startActivityForResult(takePictureIntent, SELECT_PICTURE_FROM_CAMERA);

            }
        }
    }


    public void selectImageFromGallery() {

        Utils.TEMP_IMAGE_FILE = createImageFile();
        requestCode = SELECT_PICTURE_FROM_GALLERY;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Profile Picture"), SELECT_PICTURE_FROM_GALLERY);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Permission to use Camera", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Conf.MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            takePhotoWithCamera();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Permission to read Storage", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Conf.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            selectImageFromGallery();
        }
    }

    boolean accessingFilePermissionForCamera;

    @TargetApi(Build.VERSION_CODES.M)
    private void getFilePermissionForCameraOutput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            accessingFilePermissionForCamera = true;
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Permission to read Storage", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Conf.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int[] size = getSize();
        if (requestCode == this.requestCode) {

            if (resultCode == RESULT_OK) {

                if (props.isNeedsCrop()) {//Pass to the cropper
                    if (requestCode == ScalerActivity.SELECT_PICTURE_FROM_CAMERA) {
                        process(currentUri , size[0]);
                    } else if (requestCode == ScalerActivity.SELECT_PICTURE_FROM_GALLERY) {
                        currentUri = data.getData();
                        process(currentUri , size[0]);
                    }
                } else {//Send to the original caller

                    String path = Utils.TEMP_IMAGE_FILE.getAbsolutePath();

                    if (requestCode == ScalerActivity.SELECT_PICTURE_FROM_GALLERY) {
                        currentUri = data.getData();
                        Bitmap image = ImageUtilities.decodeSampledBitmapFromUri(ScalerActivity.this, currentUri, size[0], true);
                        ImageUtilities.saveImage(image , path);
                    }

                    data = new Intent();
                    data.setData(currentUri);
                    data.putExtra(ImagePicker.RESULT_FILE_PATH, path);
                    setResult(RESULT_OK, data);
                    finish();
                }

            } else {
                setResult(RESULT_CANCELED, null);
                Log.d(getClass().getName(), "The file uri for the image is not found... 2");
                finish();
            }


        }

    }

    private void process(Uri uri, int size){
       Bitmap image = ImageUtilities.decodeSampledBitmapFromUri(this, uri, size, true);
        if(image != null){
            scalant.setImage(image);
            loadProps();
        }else{
            setResult(RESULT_CANCELED, null);
            finish();
        }

        //   scalant.setImageURI(currentUri);//works on all versions.
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Conf.MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoWithCamera();
            } else {
                Utils.showLongToast(ScalerActivity.this, "Permission denied");
            }
        } else if (requestCode == Conf.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();

            } else {
                Utils.showLongToast(ScalerActivity.this, "Permission denied.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public int[] getSize() {
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return new int[]{size.x, size.y};
        } else {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            return new int[]{display.getWidth(), display.getHeight()};
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    public void createStorageDir() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        croppersFolder = new File(storageDir, "croppers");
        if (!croppersFolder.exists()) {
            croppersFolder.mkdir();
        }
    }

    private File createImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    croppersFolder      /* directory */
            );


            // Save a file: path for use with ACTION_VIEW intents

            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
