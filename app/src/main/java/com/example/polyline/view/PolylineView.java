package com.example.polyline.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;


import com.example.polyline.imageloader.BitmapLrucache;
import com.example.polyline.bean.PolyBean;
import com.example.polyline.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myt on 2017/5/31.
 */
public class PolylineView extends View{

    private Paint timePaint;//底部时间
    private Paint sensorPaint;//温度
    private Paint linePaint;//底部横线
    private Paint paintEffect;//垂直虚线
    private Path pathEffect;

    private int timeColor = Color.BLACK;

    private int sensorColor = Color.GRAY;

    private int sensorSize = 2;

    private int lineSize = 3;

    private int lineColor = Color.GRAY;

    private float leftX = 0;//左边界

    private float rightX = 0;//右边界
    private int mHeight = 690;//控件的高度

    private List<PointF> sensors = null;//温度坐标

    private int timeSize = 39;//

    float bottomLineY = 0;

    int textY = 0;//时间的Y

    int textWidth = 0;//第一个时间的宽度

    private int timeSpace = 100;//时间间距

    private int centerSpace = 220;//一小时的距离

    private int lineStopX = 0;


//    private Bitmap bitmap;

    private Paint bitMapPaint = null;

    private int bitmapScale = 4;

    private List<Float> araeList = new ArrayList<>();

    private List<String> weathers = new ArrayList<>();

    private VelocityTracker velocityTracker;
    private float maximumVelocity;
    private float minimumVelocity;

    private Flinger flinger;
    private int maxWidth;
    private String[] words;
    private int bottomPadding = 10;//icon距离底部的距离
    private BitmapLrucache bitmapLrucache;
    private int bmp_velocity = 2;//滑到边界bitmap为1/2的速度
    private int bottomLinePadding = 20;//底部横线距离时间的距离
    public PolylineView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 底部文字字号
     * @param txtSize
     */
    public void setTimeTxtSize(int txtSize) {
        this.timeSize = txtSize;
    }

    /**
     *  底部文字颜色
     * @param timeColor
     */
    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    /**
     * 温度线条的颜色
     * @param sensorColor
     */
    public void setSensorColor(int sensorColor) {
        this.sensorColor = sensorColor;
    }

    /**
     * 温度线条的size
     * @param sensorSize
     */
    public void setSensorSize(int sensorSize) {
        this.sensorSize = sensorSize;
    }

    /**
     * 底部横线的颜色
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * 底部横线的size
     * @param lineSize
     */
    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public PolylineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PolylineView);
        if (typedArray != null) {
            timeSize = typedArray.getInteger(R.styleable.PolylineView_timeSize,39);
            timeColor = typedArray.getColor(R.styleable.PolylineView_timeColor,Color.BLACK);
            sensorColor = typedArray.getColor(R.styleable.PolylineView_sensorColor,Color.GRAY);
            sensorSize = typedArray.getInteger(R.styleable.PolylineView_sensorSize,2);
            lineColor = typedArray.getColor(R.styleable.PolylineView_lineColor,Color.GRAY);
            lineSize = typedArray.getInteger(R.styleable.PolylineView_lineSize,3);
        }
        init(context);
    }
    private void init(Context context){
        this.flinger = new Flinger(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        sensors = new ArrayList<>();

        timePaint = new Paint();


        timePaint.setColor(timeColor);
        timePaint.setTextSize(timeSize);

        sensorPaint = new Paint();//温度
        sensorPaint.setColor(sensorColor);
        sensorPaint.setAntiAlias(true);
        sensorPaint.setStrokeWidth(sensorSize);

        linePaint = new Paint();//底部横线
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineSize);


        DashPathEffect effect = new DashPathEffect(new float[] { 10,10 }, 1);
        paintEffect = new Paint();//垂直虚线
        paintEffect.setStyle(Paint.Style.STROKE);
        paintEffect.setStrokeWidth(2);
        paintEffect.setColor(Color.GRAY);
        paintEffect.setAntiAlias(true);
        paintEffect.setPathEffect(effect);
        pathEffect = new Path();

        bitMapPaint = new Paint();//bitmap
        bitMapPaint.setFilterBitmap(true);
    }
    public void addBitmapLrucache(BitmapLrucache bitmapLrucache){
        this.bitmapLrucache = bitmapLrucache;
    }
    private Bitmap getWeatherBmp(String weather){
     return bitmapLrucache.loadBitmapToImageView(weather);
//        return BitmapFactory.decodeResource(getResources(), IconUtils.getIcon(weather));
    }
    private void drawBitmap(Canvas canvas){
        bitMapPaint.reset();
        for (int i = 0; i <araeList.size() -1;i++){

            float startX = araeList.get(i);
            float stopX = araeList.get(i+1);
            Bitmap bitmap= getWeatherBmp(weathers.get(i));
            float y = bottomLineY - bottomPadding - bitmap.getHeight()/bitmapScale;
            float velocityRight =getVelocity(startX,stopX);
            float velocityLeft = getVelocity2(startX,stopX,bitmap.getWidth()/bitmapScale);
            float left = startX +(stopX - startX)/2 - (bitmap.getWidth())/(bitmapScale *2)- velocityRight+ velocityLeft;
            float right = left + bitmap.getWidth()/bitmapScale;


            Rect dstRect = new Rect((int)left, (int) y, (int) right, (int) (y + bitmap.getHeight()/bitmapScale));
            if (isVisible(left+getGlideX()) || isVisible(right+getGlideX())) {
                canvas.drawBitmap(bitmap, null, dstRect, bitMapPaint);
            }
        }
    }
    private float getVelocity(float startX,float stopX){
        float velocity = 0;

        if (startX + getGlideX()<= 0){//向左滑动左边界小于0
            velocity = (startX + getGlideX())/bmp_velocity;//offset/2

        }
        if (stopX +getGlideX() >= getMeasuredWidth()){//向右滑动右边界大于getMeasuredWidth()
            velocity = (stopX +getGlideX() - getMeasuredWidth())/bmp_velocity;//offset/2
        }
        Log.i("getVelocityRight","startX = "+startX);
        return velocity;
    }
    private float getVelocity2(float startX, float stopX,int width){
        float velocity = 0;
        if (stopX + getGlideX() -10 < width){//向左滑动右边界 -10 < width
            velocity = (stopX + getGlideX() -10 - width)/bmp_velocity;
        }
        if (startX + getGlideX() +width +10> getMeasuredWidth()){//向右滑动左边界判断
            velocity = (startX + getGlideX() +width - getMeasuredWidth()+10)/bmp_velocity;
        }
        return velocity;
    }





    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawTime(canvas);//底部时间

        drawLine(canvas);//底部横线

        drawSensor(canvas);//温度

        drawVertical(canvas);//垂直虚线

        drawBitmap(canvas);//bitmap

    }

    /**
     * 判断是否在屏幕内
     * @param x
     * @return
     */
    private boolean isVisible(float x){
        if (x >= 0 && x <= getMeasuredWidth()){
            return true;
        }
        return false;
    }

    private void drawSensor(Canvas canvas){
        for (int i = 0;i < sensors.size() - 1;i++ ) {
            PointF startF = sensors.get(i);
            PointF stopF = sensors.get(i+1);
            float startX = textWidth/2 + startF.x;
            float startY = bottomLineY - startF.y;
            float stopX = textWidth/2 + stopF.x;
            float stopY = bottomLineY - stopF.y;
            if (stopX + getGlideX() > getMeasuredWidth()){
                float a = Math.abs(stopX - startX);
                double b = -(stopY - startY);
                double height = b / a * Math.abs((getMeasuredWidth()) - (startX + getGlideX()));
                stopY = (float) Math.abs(startY - height);
                stopX = stopX - (stopX + getGlideX() - (getMeasuredWidth()));

            }
            if (startX + getGlideX() <= 0){
                float a = Math.abs(stopX - startX);
                float b = -(startY - stopY);
                double height = b / a * Math.abs(stopX - startX - (0 -(startX + getGlideX())));
                startY = (float) Math.abs(stopY - height);
                startX = startX + (0 - (startX + getGlideX()));

            }
            if (isVisible(startX +getGlideX()) && isVisible(stopX + getGlideX()) && isVisible(stopX + getGlideX()) && isVisible(startX + getGlideX())) {
                Log.i("drawSensor","startX = "+i);
                canvas.drawLine(startX, startY, stopX, stopY, sensorPaint);
            }
        }
    }
    private int getGlideX(){
        return -getScrollX();
    }
    private void drawVertical(Canvas canvas){
        araeList.clear();
        pathEffect.reset();
        for(int i = 0;i< sensors.size();i++){
            if (i %2 !=0){
                continue;
            }
            PointF startF = sensors.get(i);
            float drawX = textWidth/2 + startF.x;
            float startX = drawX;
            float startY = bottomLineY - startF.y;
            araeList.add(drawX);
            if (isVisible(startX+getGlideX())) {
                pathEffect.moveTo(startX, startY);
                pathEffect.lineTo(startX, bottomLineY);
                canvas.drawPath(pathEffect, paintEffect);
            }
        }
    }
    private void drawLine(Canvas canvas){
        bottomLineY = textY - bottomLinePadding;//
        float startX = textWidth/2;
        if (-getGlideX() > startX){
            startX = 0;
        }
        float stopX = getMeasuredWidth() - getGlideX();

        canvas.drawLine(startX, bottomLineY, stopX, bottomLineY, linePaint);
    }
    private void measureText(String text){

        Rect txtRect = getRectSize(timePaint,text);
        textY = mHeight - txtRect.height();
        textWidth = txtRect.width();
    }
    private void drawTime(Canvas canvas){
        maxWidth = 0;
        int textX = 0;
        for (int i = 0; i < words.length; i++) {
            String text = words[i];
            if (isVisible(textX + getGlideX())  || isVisible(textX +getGlideX() +textWidth)) {
                canvas.drawText(text, textX, mHeight, timePaint);
            }
            textX = textX  + 2 * timeSpace;
        }
        maxWidth = textX -  2 * timeSpace + textWidth;//
    }


    public void addSensor(List<PolyBean> polyBeans){
        words = new String[polyBeans.size()];
        int textX = 0;
        for (int i = 0;i<polyBeans.size(); i++){
            PointF pointF = new PointF(textX,polyBeans.get(i).getNum());
            sensors.add(pointF);
            words[i] = polyBeans.get(i).getWord();
            weathers.add(polyBeans.get(i).getWeather());
            textX = textX  + 2 * timeSpace;
        }
        measureText(words[0]);
        invalidate();
    }
    float downX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        float x = event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!flinger.isFinished()) { // 如果正在滚动马上停止
                    flinger.forceFinished();
                }
                downX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int offset = (int) (downX -event.getX());
                scrollBy(offset,0);
                downX = x;
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = this.velocityTracker;
                velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (Math.abs(velocityX) > minimumVelocity) {
                    flinger.start(getScrollX(), 0, velocityX, 0,
                            getMaxScrollX(), 0);
                } else {
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }

                break;
        }
        return true;
    }


    /**
     * 对超出范围进行判断
     */
    public void scrollBy(int dx, int dy) {
        if (getScrollX() + dx > getMaxScrollX()) {//超出最大范围
            super.scrollBy(getMaxScrollX() - getScrollX(), 0);
        } else if (getScrollX() + dx < 0) {//超出最小范围
            super.scrollBy(-getScrollX(), 0);
        } else {
            super.scrollBy(dx, 0);
        }
    }
    /**
     * 获取最大的滑动距离
     *
     * @return
     */
    public int getMaxScrollX() {
        if (maxWidth - getMeasuredWidth() > 0) {
            return (maxWidth - getMeasuredWidth());
        } else {
            return 0;
        }
    }
    private Rect getRectSize(Paint paint, String str){
        Rect rect = new Rect();
        //返回包围整个字符串的最小的一个Rect区域
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect;
    }

    /**
     * 控制滚动的线程
     * @author pangff
     */
    private class Flinger implements Runnable {
        private final Scroller scroller;
        private int lastX = 0;
        private int lastY = 0;

        Flinger(Context context) {
            scroller = new Scroller(context);
        }

        void start(int initX, int initY, int initialVelocityX,
                   int initialVelocityY, int maxX, int maxY) {
            scroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0,maxX, 0, maxY);
            lastX = initX;
            lastY = initY;
            post(this);
        }

        public void run() {
            if (scroller.isFinished()) {
                return;
            }
            boolean more = scroller.computeScrollOffset();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            int diffX = lastX - x;
            int diffY = lastY - y;
            if (diffX != 0 || diffY != 0) {
                scrollBy(diffX, diffY);
                lastX = x;
                lastY = y;
            }
            if (more) {
                post(this);
            }
        }

        boolean isFinished() {
            return scroller.isFinished();
        }

        void forceFinished() {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
        }
    }



}
