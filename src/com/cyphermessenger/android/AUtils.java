package com.cyphermessenger.android;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by paolo on 27/05/14.
 */
public class AUtils {

    public static void longToast(int string, Context ctx) {
        new Toast(ctx).makeText(ctx, string, Toast.LENGTH_LONG).show();
    }

    public static void shortToast(int string, Context ctx) {
        new Toast(ctx).makeText(ctx, string, Toast.LENGTH_SHORT).show();
    }

    public static void shortTopToast(int string, Context ctx) {
        Toast t = new Toast(ctx).makeText(ctx, string, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP, 0, 0);
        t.show();
    }

}
