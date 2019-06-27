/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.itis.libs.imagepick.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MRSJIBOYE
 */
public class ImageUtilities {

    /**
     * Always saves the image in PNG format
     *
     * @param bitmap   The image to save
     * @param filePath The full path to the destination file of the image. The
     *                 extension must be with .png or .jpg
     */
    public static boolean saveImagePNG(Bitmap bitmap, String filePath) {
        FileOutputStream out = null;
        boolean result = false;
        try {

            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is
            // your
            // Bitmap
            // instance
            // PNG is a lossless format, the compression factor (100) is
            // ignored
            out.flush();
            result = true;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Checks the extension of the image and uses it to
     * guess the format in which the image should be saved, whether jpg or png.
     *
     * @param bitmap   The image to save
     * @param filePath The full path to the destination file of the image. The
     *                 extension must be with .png or .jpg
     */
    public static boolean saveImage(Bitmap bitmap, String filePath) {
        FileOutputStream out = null;
        boolean result = false;
        try {

            if (filePath.toLowerCase().endsWith(".png")) {
                out = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is
                // your
                // Bitmap
                // instance
                // PNG is a lossless format, the compression factor (100) is
                // ignored
                out.flush();
                result = true;
            } else if (filePath.toLowerCase().endsWith(".jpg")) {
                out = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is
                out.flush();
                result = true;
            } else {
                out = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is
                out.flush();
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * @param imageBytes The byte array of an image.
     * @param wid        The width of the image.
     * @param hei        The height of the image.
     * @return an image from the byte array.
     */
    public static Bitmap loadImage(byte[] imageBytes, int wid, int hei) {
        Bitmap map = ImageUtilities.decodeSampledBitmapFromByteArray(imageBytes,
                wid, hei);
        return map;
    }

    /**
     * @param imageBytes The byte array of an image file
     * @return a small image from the file.
     */
    public static Bitmap loadIcon(byte[] imageBytes) {
        return decodeSampledBitmapFromByteArray(imageBytes, Utils.IMAGE_ICON_SIZE, true);
    }

    /**
     * @param filepath The absolute path to the image file
     * @return a small image from the file.
     */
    public static Bitmap loadIcon(String filepath) {
        File file = new File(filepath);
        return decodeSampledBitmapFromFile(file, Utils.IMAGE_ICON_SIZE, true);
    }

    /**
     * @param map The bitmap to scale as an icon.
     * @return a small image from the file.
     */
    public static Bitmap loadIcon(Bitmap map) {
        return scaleImage(map, Utils.IMAGE_ICON_SIZE, true);
    }

    /**
     * Loads an image stored in the assets folder.
     *
     * @param pathInAssetsFolder The path to the image location in the assets folder
     * @return image
     */
    public static Bitmap loadImage(Activity activity, String pathInAssetsFolder) {

        AssetManager assetManager = activity.getAssets();
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(pathInAssetsFolder);
            bitmap = BitmapFactory.decodeStream(inputStream);
        }// end try
        catch (IOException exception) {
            return null;
        }
        return bitmap;
    }

    /**
     * @param file the file object
     * @return the Bitmap
     */
    public static Bitmap loadImage(File file) {
        Bitmap bitmap = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(
                    file));
            // bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);



        }// end try
        catch (Exception exception) {
            return null;
        } finally {
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * @param bmp the image whose bytes are needed
     * @return an array containing the bytes of the image, if the image size is above 300 KB,
     * it returns the byte array for an equivalent image of size 300 KB due to memory constraints.
     */

    public static byte[] getImageBytes(Bitmap bmp) {
        return getImageBytes(bmp, 300000);
    }// end method.

    /**
     * @param bmp                  the image whose bytes are needed
     * @param threshHoldMemorySize the max size of the supplied image for which this method will try to accurately
     *                             compute the byte array. For images with memory above this size, this method
     *                             will try to compute the byte array for an equivalent image of size 300 KB due to memory constraints.
     * @return an array containing the bytes of the image, if the image size is above 300 KB,
     * it returns the byte array for an equivalent image of size 300 KB due to memory constraints.
     */

    public static byte[] getImageBytes(Bitmap bmp, int threshHoldMemorySize) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        if (bytes.length <= threshHoldMemorySize) {
            return bytes;
        } else {
            int percent = (int) ((threshHoldMemorySize / bytes.length) * 100.0);
            ByteArrayOutputStream str = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, percent, stream);
            return str.toByteArray();
        }

    }// end method.

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * Scales this image at constant aspect ratio.
     *
     * @param map                 The bitmap to scale
     * @param newDimension        The width of the bitmap or its height
     * @param newDimensionIsWidth Set this parameter to true if newDimension
     *                            refers to the new width of the Bitmap, else set it to false if it refers to the
     *                            new height.
     */
    public static Bitmap scaleImage(Bitmap map, int newDimension, boolean newDimensionIsWidth) {


        float oldWidth = map.getWidth();
        float oldHeight = map.getHeight();


        float newWidth = 1;
        float newHeight = 1;

        float aspectRatio = oldWidth / oldHeight;
        float scaleWidth;
        float scaleHeight;

        if (newDimensionIsWidth) {
            newWidth = newDimension;
            newHeight = newWidth / aspectRatio;
        } else {
            newHeight = newDimension;
            newWidth = newHeight * aspectRatio;
        }


        scaleWidth = newWidth / oldWidth;
        scaleHeight = newHeight / oldHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resized = Bitmap.createBitmap(map, 0, 0, (int) oldWidth, (int) oldHeight, matrix, false);
        map.recycle();
        map = null;

        return resized;
    }

    /**
     * Scales this image at constant aspect ratio.
     *
     * @param imageResourceId     The resource id of the bitmap to scale
     * @param newDimension        The width of the bitmap or its height
     * @param newDimensionIsWidth Set this parameter to true if newDimension
     *                            refers to the new width of the Bitmap, else set it to false if it refers to the
     *                            new height.
     * @param context             The Context that is loading the image.
     */
    public static Bitmap scaleImageResource(int imageResourceId, int newDimension, boolean newDimensionIsWidth,
                                            Context context) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap map = BitmapFactory.decodeResource(context.getResources(), imageResourceId, options);
        return scaleImage(map, newDimension, newDimensionIsWidth);
    }

    /**
     * @param data The bytes of the image
     * @return an array containing the width of the image in index 0
     * and the height of the image in index 1.
     */
    public static int[] calculateImageSize(byte[] data) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * @param imageData The image file.
     * @return an array containing the width of the image in index 0
     * and the height of the image in index 1.
     */
    public static int[] calculateImageSize(File imageData) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageData.getAbsolutePath(), options);

        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * @param imageData The image inputstream..
     * @return an array containing the width of the image in index 0
     * and the height of the image in index 1.
     */
    public static int[] calculateImageSize(InputStream imageData) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageData, null, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * @param data The file descriptor of the image
     * @return an array containing the width of the image in index 0
     * and the height of the image in index 1.
     */
    public static int[] calculateImageSize(FileDescriptor data) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(data, null, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * @param data The bytes of the image
     * @return The ratio of the width of the image to its height as a floating point number
     */
    public static double calculateAspectRatio(byte[] data) {
        int[] sizes = calculateImageSize(data);
        return (1.0 * sizes[0]) / (1.0 * sizes[1]);
    }

    /**
     * @param imageData The image file.
     * @return The ratio of the width of the image to its height as a floating point number
     */
    public static double calculateAspectRatio(File imageData) {
        int[] sizes = calculateImageSize(imageData);
        return (1.0 * sizes[0]) / (1.0 * sizes[1]);
    }

    /**
     * @param imageData The image inputstream.
     * @return The ratio of the width of the image to its height as a floating point number
     */
    public static double calculateAspectRatio(InputStream imageData) {
        int[] sizes = calculateImageSize(imageData);
        return (1.0 * sizes[0]) / (1.0 * sizes[1]);
    }

    /**
     * @param fd The file descriptor
     * @return The ratio of the width of the image to its height as a floating point number
     */
    public static double calculateAspectRatio(FileDescriptor fd) {
        int[] sizes = calculateImageSize(fd);
        return (1.0 * sizes[0]) / (1.0 * sizes[1]);
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inMutable = true;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(File data,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(data.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inMutable = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(data.getAbsolutePath(), options);
    }


    /**
     * Allows the user flexibility to scale the image at constant aspect ratio.
     *
     * @param file             The image file of the Bitmap to be decoded.
     * @param dimension        The new dimension to be applied. It may be width or height, depending on the value
     *                         of <code>dimensionIsWidth</code>
     * @param dimensionIsWidth The parameter that determines if the dimension supplied in<code>dimension</code> is the
     *                         new width or the height
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(File file, int dimension, boolean dimensionIsWidth) {
        if (file == null || !file.exists()) {
            return Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
        }
        double aspectRatio = calculateAspectRatio(file);
        double width, height;

        if (dimensionIsWidth) {
            width = dimension;
            height = dimension / aspectRatio;
        } else {
            height = dimension;
            width = dimension * aspectRatio;
        }
        return decodeSampledBitmapFromFile(file, (int) width, (int) height);
    }

    /**
     * Allows the user flexibility to scale the image at constant aspect ratio.
     *
     * @param data             The byte array of the Bitmap to be decoded.
     * @param dimension        The new dimension to be applied. It may be width or height, depending on the value
     *                         of <code>dimensionIsWidth</code>
     * @param dimensionIsWidth The parameter that determines if the dimension supplied in<code>dimension</code> is the
     *                         new width or the height
     * @return
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int dimension, boolean dimensionIsWidth) {
        double aspectRatio = calculateAspectRatio(data);
        double width, height;

        if (dimensionIsWidth) {
            width = dimension;
            height = dimension / aspectRatio;
        } else {
            height = dimension;
            width = dimension * aspectRatio;
        }
        return decodeSampledBitmapFromByteArray(data, (int) width, (int) height);
    }


    public static Bitmap decodeSampledBitmapFromStream(InputStream data,
                                                       int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(data, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inMutable = true;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(data, null, options);
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream data,
                                                       int dimension, boolean dimensionIsWidth) {

        double aspectRatio = calculateAspectRatio(data);
        double width, height;

        if (dimensionIsWidth) {
            width = dimension;
            height = dimension / aspectRatio;
        } else {
            height = dimension;
            width = dimension * aspectRatio;
        }

        return decodeSampledBitmapFromStream(data, (int) width, (int) height);
    }


    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri,
                                                    int reqWidth, int reqHeight) {


        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");

            if (fileDescriptor == null) {
                return null;
            }
            double aspectRatio = calculateAspectRatio(fileDescriptor.getFileDescriptor());

            double width, height;


            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);


            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inMutable = true;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri,
                                                    int dimension, boolean dimensionIsWidth) {


        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");

            if (fileDescriptor == null) {
                return null;
            }
            double aspectRatio = calculateAspectRatio(fileDescriptor.getFileDescriptor());

            double width, height;


            if (dimensionIsWidth) {
                width = dimension;
                height = dimension / aspectRatio;
            } else {
                height = dimension;
                width = dimension * aspectRatio;
            }

            return decodeSampledBitmapFromUri(context, uri, (int) width, (int) height);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param data The byte array of the image.
     * @return An array of 2 entries. Index 0 has the width of the image, index
     * 1 has its height.
     */
    public static double[] getImageDimensions(byte[] data) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,
                options);
        return new double[]{bmp.getWidth(), bmp.getHeight()};
    }

    /**
     * @param bmp the image whose bytes are needed
     * @return an array containing the bytes of the image.
     */
    public static Bitmap getImageFromBytes(byte[] bmp) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        return BitmapFactory.decodeByteArray(bmp, 0, bmp.length, options);
    }


}// end class