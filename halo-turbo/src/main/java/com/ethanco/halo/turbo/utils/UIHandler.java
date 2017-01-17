package com.ethanco.halo.turbo.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * UI操作的Handler
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class UIHandler {
    private static Handler handler;

    public static Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }
}
