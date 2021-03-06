package com.example.tal.shocker.Utility;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tal.shocker.R;

public class Utils {


    public static void configureFab(View fabButton) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabButton.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    int fabSizeWidth = view.getContext().getResources().getDimensionPixelSize(R.dimen.fab_width);
                    int fabSizeHighet = view.getContext().getResources().getDimensionPixelSize(R.dimen.fab_highet);
                   outline.setOval(0, 0, fabSizeWidth, fabSizeHighet);
                    //outline.setRect(0,0,fabSizeWidth,fabSizeHighet);
                }
            });
        } else {
            ((ImageButton) fabButton).setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}
