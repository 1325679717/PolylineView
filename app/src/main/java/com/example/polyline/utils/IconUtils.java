package com.example.polyline.utils;

import com.example.polyline.R;

/**
 * Created by Administrator on 2017/8/16.
 */

public class IconUtils {
    public static int getIcon(String weather){
        int res = R.mipmap.wic_clear_d_new;
        switch (weather){
            case "晴天":
                res = R.mipmap.wic_clear_d_new;
                break;
            case "多云":
                res = R.mipmap.wic_cloudy_d_new;
                break;
        }
        return res;
    }
}
