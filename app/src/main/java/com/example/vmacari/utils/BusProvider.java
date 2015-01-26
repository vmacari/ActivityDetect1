package com.example.vmacari.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();
    private static final Handler mHandler = new Handler(Looper.getMainLooper());


    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }


    public static void post (final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getInstance().post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override public void run() {
                    getInstance().post(event);
                }
            });
        }
    }


}