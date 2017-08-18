package com.example.polyline.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/8.
 */

public class PolyBean implements Serializable {
    private int num;
    private String word;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    private String weather;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
