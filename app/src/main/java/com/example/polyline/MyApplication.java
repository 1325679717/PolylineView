package com.example.polyline;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/8/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * @description
     *
     * @param context
     * @return 得到需要分配的缓存大小，这里用八分之一的大小来做
     */
    public int getMemoryCacheSize() {
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        return 1024 * 1024 * memClass / 8;
    }
}
