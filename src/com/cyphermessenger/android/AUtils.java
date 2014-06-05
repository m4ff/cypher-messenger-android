package com.cyphermessenger.android;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by paolo on 27/05/14.
 */
public class AUtils {

    public static void longToast(int string, Context ctx) {
        Toast t = new Toast(ctx).makeText(ctx, string, Toast.LENGTH_LONG);
        t.setGravity(Gravity.TOP, 0, dpToPx(55, ctx));
        t.show();
    }

    public static int dpToPx(int dp, Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static void shortToast(int string, Context ctx) {
        Toast t = new Toast(ctx).makeText(ctx, string, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP, 0, dpToPx(55, ctx));
        t.show();
    }

}
