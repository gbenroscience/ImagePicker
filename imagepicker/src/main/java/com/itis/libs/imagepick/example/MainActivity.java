package com.itis.libs.imagepick.example;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.itis.libs.imagepick.utils.ImagePickedListener;
import com.itis.libs.imagepick.utils.ImagePicker;
import com.itis.libs.imagepick.R;
import com.itis.libs.imagepick.utils.ImageUtilities;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ImagePickedListener {

    AppCompatImageView pictureView;
    AppCompatImageView addBtn;

    private ImagePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);


        init();



    }

    private void init() {

        pictureView = findViewById(R.id.image_view);
        addBtn = findViewById(R.id.add_btn);

        picker = ImagePicker.createPicker();

        picker.bgColor("#000000").cropperBorderColor("#FFFF99").headerColor("#000088").needsCrop(true).showPreview(true).showGrid(false).cropRectThickness(4);

        picker.setImagePickedListener(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //picker.openGallery(MainActivity.this , true);
                // picker.openCamera(MainActivity.this , true);
                picker.openChooser(MainActivity.this);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picker.handleActivityResult(requestCode, resultCode, data, this);
    }

    @Override
    public void onImagePicked(Uri uri, File file) {
        pictureView.setImageBitmap(ImageUtilities.decodeSampledBitmapFromFile(file, pictureView.getWidth(), true));
    }

    @Override
    public void onPickerError(int errorCode) {

        if (errorCode == ImagePickedListener.ERROR_BITMAP_NOT_LOADED) {

        } else if (errorCode == ImagePickedListener.ERROR_LISTENER_NOT_SET) {

        }

    }
}
