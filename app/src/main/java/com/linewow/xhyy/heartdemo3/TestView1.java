package com.linewow.xhyy.heartdemo3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by LXR on 2016/12/1.
 */
public class TestView1 extends View {

    private RectF rectFBg;
    private float mAnimatedValue=1f;
    private float startYScale=0.27f;
    private String TAG="TestView1";
    private Paint mPaintLike;
    private Paint paint;
    private float loveSize = 0.8f;

    public TestView1(Context context) {
        this(context,null);
    }

    public TestView1(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TestView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST
                && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(dip2px(30), dip2px(30));
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(heightSpecSize, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, widthSpecSize);
        }
    }



    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintLike=new Paint();
        mPaintLike.setColor(Color.RED);
        mPaintLike.setStyle(Paint.Style.FILL);
        mPaintLike.setAntiAlias(true);
        paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);


        rectFBg = new RectF(0, 0,
                getMeasuredWidth(),
                getMeasuredHeight());


        RectF rectFlove = new RectF();
        rectFlove.top = rectFBg.centerY() - (rectFBg.height() / 2f + paint.getStrokeWidth()) * mAnimatedValue;//* 0.5f;
        rectFlove.bottom = rectFBg.centerY() + (rectFBg.height() / 2f + paint.getStrokeWidth()) * mAnimatedValue;// * 0.5f;
        rectFlove.left = rectFBg.centerX() - (rectFBg.width() / 2f + paint.getStrokeWidth()) * mAnimatedValue;// * 0.5f;
        rectFlove.right = rectFBg.centerX() + (rectFBg.width() / 2f + paint.getStrokeWidth()) * mAnimatedValue;//* 0.5f;


        float realWidth = rectFlove.width();
        float realHeight = rectFlove.height();
        rectFlove.top = rectFlove.top + realHeight * (1 - 0.8f) / 2f;
        Path path = new Path();


        path.moveTo((float) (0.5 * realWidth) + rectFlove.left, (float) (startYScale * realHeight) + rectFlove.top);
        path.cubicTo((float) (0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight + rectFlove.top),
                (float) (-0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (realHeight * 0.8 + rectFlove.top));



        path.cubicTo((float) (realWidth + 0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (realWidth - 0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (startYScale * realHeight) + rectFlove.top);
        path.close();
        canvas.drawPath(path,paint);
        
        drawLoves(canvas);

    }

    private void drawLoves(Canvas canvas) {


        RectF rectFlove = new RectF();
        rectFlove.top = rectFBg.centerY() - (rectFBg.height() / 2f + paint.getStrokeWidth()) * mAnimatedValue;//* 0.5f;
        rectFlove.bottom = rectFBg.centerY() + (rectFBg.height() / 2f + paint.getStrokeWidth()) * mAnimatedValue;// * 0.5f;
        rectFlove.left = rectFBg.centerX() - (rectFBg.width() / 2f + paint.getStrokeWidth()) * mAnimatedValue;// * 0.5f;
        rectFlove.right = rectFBg.centerX() + (rectFBg.width() / 2f + paint.getStrokeWidth()) * mAnimatedValue;//* 0.5f;


        float realWidth = rectFlove.width();
        float realHeight = rectFlove.height();
        rectFlove.top = rectFlove.top + realHeight * (1 - 0.8f) / 2f;
        Path path = new Path();


        float lastY = (float) (realHeight * 0.8 + rectFlove.top);
        float lastX = (float) (0.5 * realWidth) + rectFlove.left;
        Bitmap bitmapRight= Bitmap.createBitmap(getMeasuredWidth(), (int) lastY, Bitmap.Config.ARGB_8888);
        Canvas canvas1=new Canvas(bitmapRight);
        canvas1.rotate(-2,lastX,lastY);


        path.moveTo((float) (0.5 * realWidth) + rectFlove.left, (float) (startYScale * realHeight) + rectFlove.top);
        path.cubicTo((float) (0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight + rectFlove.top),
                (float) (-0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (realHeight * 0.8 + rectFlove.top));


        Log.e(TAG,"first-->"+((float) (0.15 * realWidth) + rectFlove.left)+"--"+((float) (-0.35 * realHeight + rectFlove.top)));


        path.cubicTo((float) (realWidth + 0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (realWidth - 0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (startYScale * realHeight) + rectFlove.top);
        path.close();
        canvas1.drawPath(path,mPaintLike);
        canvas.drawBitmap(bitmapRight,0,0,mPaintLike);
        
    }






}
