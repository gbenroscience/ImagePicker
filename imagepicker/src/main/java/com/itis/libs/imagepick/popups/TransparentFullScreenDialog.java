package com.itis.libs.imagepick.popups;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import com.itis.libs.imagepick.pages.ScalerActivity;

import java.lang.ref.WeakReference;

/**
 * Created by JIBOYE Oluwagbemiro Olaoluwa on 11/26/2017.
 */

public class TransparentFullScreenDialog extends Dialog {


    public TransparentFullScreenDialog(final ScalerActivity context, int layoutId) {
        super(context, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99757575")));
        setContentView(layoutId);

        setOwnerActivity(new WeakReference<>(context).get());

    }

    @Override
    public void show() {
        ScalerActivity activity = (ScalerActivity) getOwnerActivity();


        if (activity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }
        if (activity.isFinishing()) {
            return;
        }


        getOwnerActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    TransparentFullScreenDialog.super.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void dismiss() {
        ScalerActivity activity = (ScalerActivity) getOwnerActivity();

        if (activity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }
        if (activity.isFinishing()) {
            return;
        }


        if (!isShowing()) {
            return;
        }

        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
