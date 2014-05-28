package com.cyphermessenger.android;

import android.content.Context;
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

}
