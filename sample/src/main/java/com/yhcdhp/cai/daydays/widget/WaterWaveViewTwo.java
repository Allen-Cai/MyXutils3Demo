package com.yhcdhp.cai.daydays.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yhcdhp.cai.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * author: wanglinlin
 * <p/>
 * introduction: xxxx
 * <p/>
 * Created by jack on 2015/12/22.
 */
public class WaterWaveViewTwo extends View {


    private ArrayList<Point> mPointsList;
    private ArrayList<Point> mPointsListTwo;
    private Timer mTimer;
    private Paint mPaint;
    private Paint mPaintTwo;
    private Paint mTextPaint;
    private Path mWaterWavePath;
    private Path mWaterWavePathTwo;

    //    private boolean isFirst = true;
    private int mViewHeight;
    private int mViewWidth;
    private float mWaterLine;
    private float mWaterWaveHeight;
    private float mWaterWaveWidth;

    private float mUnitLength = 0.0f;
    private float mWaterWaveMoveLength;
    private float mWaterWaveMoveLengthTwo;
    private boolean mIsReachTargetPoint = false;
    private int mTargetPoint = 0;
    /**
     * 刷新的事件是每隔 10 ms 绘制一次
     */
    private long refresh_time = 10;

    /**
     * 设置百分比，默认是0，targetPoint为整数，值为 0 - 100
     */
    public void setTargetPoint(int targetPoint) {
        mTargetPoint = targetPoint;
    }

    public void reset() {
        /**水位线*/
        mWaterLine = mViewHeight;
        mIsReachTargetPoint = false;
    }

    private Handler mWaterWaveHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            /**记录波长已经移动了的距离*/
            mWaterWaveMoveLength = mWaterWaveMoveLength + mUnitLength;
            mWaterWaveMoveLengthTwo = mWaterWaveMoveLengthTwo + 2 * mUnitLength;

            /**水位上升*/
            if (!mIsReachTargetPoint) {
                mWaterLine = mWaterLine - 1f;
            }
            /*(int)((1-mWaterLine/mViewHeight)*100) >= mTargetPoint;*/
            int mTargetLine = (int) ((1 - mTargetPoint / 100.0f) * mViewHeight);
//            LogUtils.d("wll", "mTargetLine = " + mTargetLine + ",mTargetPoint = " + mTargetPoint + ",mViewHeight = " + mViewHeight + ",mIsReachTargetPoint = " + mIsReachTargetPoint);
            if (mWaterLine < mTargetLine) {
                mWaterLine = mTargetLine;
            }

            if (mWaterLine < 0) {
                mWaterLine = 0;
            }
            mLeftSide = mLeftSide + mUnitLength;

            /**平移整个波浪*/
            for (int i = 0; i < mPointsList.size(); i++) {
                mPointsList.get(i).setX(mPointsList.get(i).getX() + mUnitLength);
                mPointsListTwo.get(i).setX(mPointsListTwo.get(i).getX() + 2 * mUnitLength);
                switch (i % 4) {
                    case 0:
                        mPointsList.get(i).setY(mWaterLine);
                        mPointsListTwo.get(i).setY(mWaterLine);
                        break;
                    case 2:
                        mPointsList.get(i).setY(mWaterLine);
                        mPointsListTwo.get(i).setY(mWaterLine);
                        break;
                    case 1:
                        mPointsList.get(i).setY(mWaterLine + mWaterWaveHeight);
                        mPointsListTwo.get(i).setY(mWaterLine - mWaterWaveHeight);
                        break;
                    case 3:
                        mPointsList.get(i).setY(mWaterLine - mWaterWaveHeight);
                        mPointsListTwo.get(i).setY(mWaterLine + mWaterWaveHeight);

                        break;
                }
            }
            /**平移超过一个波长后，复位*/
            if (mWaterWaveMoveLength > mWaterWaveWidth) {
                mWaterWaveMoveLength = 0;
                resetPoints();
            }
            if (mWaterWaveMoveLengthTwo > mWaterWaveWidth) {
                mWaterWaveMoveLengthTwo = 0;
                resetPointsTwo();
            }

            invalidate();
        }
    };
    private int mWater_color;

    /**
     * 所有点的x坐标都还原到初始状态，也就是一个周期前的状态
     */
    private void resetPoints() {
        mLeftSide = -mWaterWaveWidth;
        for (int i = 0; i < mPointsList.size(); i++) {
            float w = (i * mWaterWaveWidth / 4) - mWaterWaveWidth;
            mPointsList.get(i).setX(w);
        }
    }

    /**
     * 所有点的x坐标都还原到初始状态，也就是一个周期前的状态
     */
    private void resetPointsTwo() {
        for (int i = 0; i < mPointsList.size(); i++) {
            float w = (i * mWaterWaveWidth / 4) - mWaterWaveWidth;
            mPointsListTwo.get(i).setX(w);
        }
    }

    private MyTimerTask mTimerTask;
    private float mLeftSide;

    public WaterWaveViewTwo(Context context) {
        super(context);
        init(context, null);
    }

    public WaterWaveViewTwo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaterWaveViewTwo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private boolean hasAttachedToWindow = true;


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        UIUtils.showShortCustomToast(getContext(), "控件被添加到窗口上");
        startWaterWave();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        UIUtils.showShortCustomToast(getContext(), "控件从窗口上移除掉");
        destroyWaterWave();
    }

    private void destroyWaterWave() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startWaterWave() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimerTask = new MyTimerTask();
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(mTimerTask, 0, refresh_time);
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    /**
     * what's the fuck  ?!! this method doesn't be called ? why!!!
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

//        UIUtils.showShortCustomToast(getContext(), "窗口获得焦点了？ -- "+hasWindowFocus);
        /**view所在窗口获得焦点就开始波动或者是去焦点停止波动*/
        if (hasWindowFocus) {
            startWaterWave();
        } else {
            destroyWaterWave();
        }

    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            /**发送空消息，执行刷新动作*/
            mWaterWaveHandler.sendMessage(mWaterWaveHandler.obtainMessage());
//            LogUtils.d("wll", "TimerTask -- " + mWaterLine);
        }
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterWaveViewTwo);

        mPointsList = new ArrayList<>();
        mPointsListTwo = new ArrayList<>();
        mTimer = new Timer();

        /**绘制波浪的笔*/
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mWater_color = typedArray.getColor(R.styleable.WaterWaveViewTwo_water_color, 0x220087EC);
        mPaint.setColor(mWater_color);/*#0087EC*/

        mPaintTwo = new Paint();
        mPaintTwo.setAntiAlias(true);
        mPaintTwo.setStyle(Paint.Style.FILL);
        mPaintTwo.setColor(mWater_color);


        /**绘制百分比文本的笔*/
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(30);

        /**水波纹的轮廓线*/
        mWaterWavePath = new Path();
        mWaterWavePathTwo = new Path();

        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getWidth();
        int height = getHeight();
        Log.d("wll", "onSizeChanged 大小改变的时候 ：宽度 -- " + width + "，高度 -- " + height);

        /**初始化所需要的点的集合*/

        mPointsList.clear();
        mPointsListTwo.clear();
//        isFirst = false;
        mViewHeight = getHeight();
        mViewWidth = getWidth();
        Log.d("wll", "测量后的宽度 onMeasure mViewWidth = " + mViewWidth);

        /**水位线*/
        mWaterLine = mViewHeight;

        /**一个完整波浪的波峰高度，这里并不会是绝对高度，只是控制点的位置*/
        mWaterWaveHeight = mViewHeight / 50.0f;

        /**一个完整波浪的宽度*/
        mWaterWaveWidth = mViewWidth / 1;


        /**每次移动的水平距离*/
        mUnitLength = 2.0f;

        /**整个静态波浪的左侧位置*/
        mLeftSide = -mWaterWaveWidth;

        /**显示区域可以显示多少个波长，不足一个波长的，填充为一个波长*/
        int n = Math.round(mViewWidth / mWaterWaveWidth + 0.5f);

//            mPointsListTwo.add(new Point(mLeftSide-mWaterWaveWidth/4,mWaterLine));

        for (int i = 0; i < (4 * n + 5); i++) {
            float x = i * mWaterWaveWidth / 4 + mLeftSide;
            float y = 0;
            float y2 = 0;
            switch (i % 4) {
                case 0:
                    y = mWaterLine;
                    y2 = mWaterLine;
                    break;
                case 2:
                    /**水平线上*/
                    y = mWaterLine;
                    y2 = mWaterLine;
                    break;
                case 1:
                    /**波谷*/
                    y = mWaterLine + mWaterWaveHeight;
                    y2 = mWaterLine - mWaterWaveHeight;

                    break;
                case 3:
                    /**波峰*/
                    y = mWaterLine - mWaterWaveHeight;
                    y2 = mWaterLine + mWaterWaveHeight;
                    break;
            }
            mPointsList.add(new Point(x, y));
            mPointsListTwo.add(new Point(x, y2));

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        LogUtils.d("wll", "onMeasure 被执行了一次 isFirst = " + isFirst);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWaterWavePath.reset();
        mWaterWavePathTwo.reset();
        int i = 0;
        /**移动到第一个点上*/
        mWaterWavePath.moveTo(mPointsList.get(i).getX(), mPointsList.get(i).getY());
        for (i = i + 1; i < mPointsList.size() - 2; i += 2) {
            /**采用贝瑟尔曲线模拟正玄波*/
            mWaterWavePath.quadTo(mPointsList.get(i).getX(), mPointsList.get(i).getY(),
                    mPointsList.get(i + 1).getX(), mPointsList.get(i + 1).getY());
        }
        /**画临近的垂直线*/
        mWaterWavePath.lineTo(mPointsList.get(mPointsList.size() - 1).getX(), mViewHeight);
        mWaterWavePath.lineTo(mPointsList.get(0).getX(), mViewHeight);
        /**自动补全线段*/
        mWaterWavePath.close();



        /*mWaterWavePathTwo.moveTo(mPointsList.get(i).getX()+10.0f, mPointsList.get(i).getY());
        for(i = i+1 ;i < mPointsList.size() - 2;i += 2){
            *//**采用贝瑟尔曲线模拟正玄波*//*
            mWaterWavePath.quadTo(mPointsList.get(i).getX()+10.0f,mPointsList.get(i).getY(),
                    mPointsList.get(i+1).getX()+10.0f,mPointsList.get(i+1).getY());
        }
        *//**画临近的垂直线*//*
        mWaterWavePathTwo.lineTo(mPointsList.get(mPointsList.size() - 1).getX()+10.0f,mViewHeight);
        mWaterWavePathTwo.lineTo(mPointsList.get(0).getX()+10.0f, mViewHeight);
        *//**自动补全线段*//*
        mWaterWavePathTwo.close();*/
        /**画整个轮廓图，并通过画笔设置为填充*/
        canvas.drawPath(mWaterWavePath, mPaint);

        i = 0;
        mWaterWavePathTwo.moveTo(mPointsListTwo.get(i).getX(), mPointsListTwo.get(i).getY());
        for (i = i + 1; i < mPointsListTwo.size() - 2; i += 2) {
            mWaterWavePathTwo.quadTo(mPointsListTwo.get(i).getX(), mPointsListTwo.get(i).getY(),
                    mPointsListTwo.get(i + 1).getX(), mPointsListTwo.get(i + 1).getY());
        }
        mWaterWavePathTwo.lineTo(mPointsListTwo.get(mPointsListTwo.size() - 1).getX(), mViewHeight);
        mWaterWavePathTwo.lineTo(mPointsListTwo.get(0).getX(), mViewHeight);
        mWaterWavePathTwo.close();


        canvas.drawPath(mWaterWavePathTwo, mPaintTwo);


        /* //调试时使用
          if (!mIsReachTargetPoint) {
            LogUtils.d("wll", "当前水的波浪高度 --  " + (1 - mWaterLine / mViewHeight) * 100);
            LogUtils.d("wll", "当前波浪控件的宽度 --  " + mViewWidth / 2 + ",当前波浪控件的实际宽度 -- " + getWidth());
        }*/
        /**画百分比*/
       /* canvas.drawText("" + (int) ((1 - mWaterLine / mViewHeight) * 100) + "%",
                mViewWidth / 2, mWaterLine + mWaterWaveHeight + (mViewHeight - mWaterLine - mWaterWaveHeight) / 2, mTextPaint);*/

        if ((int) ((1 - mWaterLine / mViewHeight) * 100) >= mTargetPoint) {
            mIsReachTargetPoint = true;
        }
    }


    private class Point {
        float x, y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}