package com.example.polyline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.polyline.bean.PolyBean;
import com.example.polyline.imageloader.BitmapLrucache;
import com.example.polyline.view.PolylineView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int[] array = {180,80,100,200,90,100,121,390,300,150,130,70,123,230,250,320,230,290,121,80,180,200,300,180};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_main);

        PolylineView polylineView = (PolylineView) findViewById(R.id.chart_view);
        polylineView.addBitmapLrucache(new BitmapLrucache(this));
        List<PolyBean> polyBeans = new ArrayList<>();


        for (int i = 0;i < array.length; i++){
            PolyBean polyBean = new PolyBean();
            polyBean.setNum(array[i]);
            polyBean.setWord(getTime(i+1));
            if (i %2 == 0){
                polyBean.setWeather("晴天");
            }else {
                polyBean.setWeather("多云");
            }
            polyBeans.add(polyBean);
        }
        polylineView.addSensor(polyBeans);
    }

    private String getTime(int i){
        String time = "0:00";
        if (i<10){
            time = "0"+i+":00";
        }else if (i>=10 && i<24){
            time = i+":00";
        }else{
            time = "00:00";
        }
        return time;
    }
}
