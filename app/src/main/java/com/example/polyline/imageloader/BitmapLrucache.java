package com.example.polyline.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.example.polyline.MyApplication;
import com.example.polyline.utils.IconUtils;

/**
 * Created by Administrator on 2017/8/16.
 */

public class BitmapLrucache {
    private LruCache<String, Bitmap> bitmapLruCache;
    private Activity act;
    public BitmapLrucache (Activity act){
        this.act = act;
        final int memoryCache = ((MyApplication) act.getApplication()).getMemoryCacheSize();
        bitmapLruCache = new LruCache<String, Bitmap>(memoryCache) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        }; // 初始化
    }
    /**
     * @description 将bitmap添加到内存中去
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapLruCache.put(key, bitmap);
        }
    }

    /**
     * @description 通过key来从内存缓存中获得bitmap对象
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return bitmapLruCache.get(key);
    }
    public Bitmap loadBitmapToImageView(String weather) {

        Bitmap bitmap = getBitmapFromMemCache(weather); // 先看这个资源在不在内存中，如果在直接读取为bitmap，否则返回null
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(act.getResources(), IconUtils.getIcon(weather));
            addBitmapToMemoryCache(weather,bitmap);
        }
        return bitmap;
    }
}
