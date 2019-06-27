package com.itis.libs.imagepick.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.itis.libs.imagepick.R;
import com.itis.libs.imagepick.BuildConfig;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.core.content.FileProvider;

public class Utils {


    public static final String BASE_PATH = BuildConfig.APPLICATION_ID+".fileprovider";






    public static final int IMAGE_BIG_SIZE = 350;//A size of 350X350 for all images

    public static final int IMAGE_POST_SIZE = 210;//A size of 150X150 for all image posts
    public static final int IMAGE_ICON_SIZE = 30;//A size of 50X50 for all image icon posts

    public static File DOWNLOADED_FILES_FOLDER;


    public static File APP_FOLDER;


    public static File TEMP_IMAGE_FILE;

    public static String APP_NAME;

    private static boolean enableLogging = true;


    public static final int MIN_UPLOAD_SIZE = 50;
    public static final int MAX_UPLOAD_SIZE = 8 * 1024 * 1024;
    /**
     * Number of best scores to save.
     */
    public static final int MAX_BEST_SCORES_SAVED = 5;

    public Utils(Context context) {

        APP_NAME = context.getResources().getString(R.string.app_name);

        TEMP_IMAGE_FILE = new File(context.getFilesDir() , "temp.jpg");


        if(!TEMP_IMAGE_FILE.exists()){
            try {
                TEMP_IMAGE_FILE.createNewFile();
            }catch (Exception e){}
        }



    }


    public static Uri getUri(File file, Context context){

        try {

            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    BASE_PATH,
                    file);

            Log.d(Utils.class.getName(), "uri>>>>>: "+fileUri.getPath() );
            Log.d(Utils.class.getName(), "BASE_PATH>>>>>: "+BASE_PATH );
            return fileUri;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Utils.class.getName(), "File Selector: The selected file can't be shared: " + file.toString());
        }
        return null;
    }



    /**
     *
     * @param view Free memory by releasing image data from
     *  views
     */
    public static void unbindDrawables(View view) {
        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
        catch (Exception e){}
    }
    /**
     *
     * @param list Free memory by releasing image data from
     * listviews
     */
    public static void unbindDrawables(ListView list) {
        try {
            int size = list.getAdapter().getCount();

            for (int i = 0; i < size; i++) {
                Object o = list.getItemAtPosition(i);
                if (o instanceof View) {
                    View view = (View) o;
                    unbindDrawables(view);
                }
            }
        }
        catch (Exception e){}
    }
    /**
     *  @param callingClass The name of the class that called this method.May be null
     * @param message The error message to log
     */
    public static void logErrorMessage(String message, Class callingClass){
        if(enableLogging) {
            String name = "unspecified_class";
            if (callingClass != null) {
                name = callingClass.getName();
            }
            Log.e("imagepicker", "Message in " + name + ":   " + message);
        }
    }

    /**
     *  @param callingClass The name of the class that called this method.May be null
     * @param message The error message to log
     */
    public static void logInformationMessage(String message, Class callingClass){
        if(enableLogging) {
            String name = "unspecified_class";
            if (callingClass != null) {
                name = callingClass.getName();
            }

                Log.e("imagepicker", "Message in " + name + ":   " + message);

        }
    }

    /**
     * @param message The error message to log
     * @param callingClass The name of the class that called this method.May be null
     */
    public static void logDebugMessage(String message, Class callingClass){
        if(enableLogging) {
            String name = "unspecified_class";
            if (callingClass != null) {
                name = callingClass.getName();
            }
                Log.e("imagepicker", "Message in " + name + ":   " + message);

        }
    }
    public static void showShortToast(final Context context, final String text) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }
        });
    }

    public static void showLongToast(final Context context, final String text) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                }
            }
        });

    }



    /**
     * @param view A view on the Activity
     * @return the {@link Activity}
     */
    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return new WeakReference<>((Activity) context).get();
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}




