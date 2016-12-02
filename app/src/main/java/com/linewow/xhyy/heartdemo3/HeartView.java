package com.linewow.xhyy.heartdemo3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.lang.ref.WeakReference;

/**
 * Created by LXR on 2016/11/30.
 */
public class HeartView extends View {
    private int edgeColor = Color.BLACK;
    private int fillColor = Color.GREEN;// Color.rgb(229, 115, 108);
    private int cracksColor = Color.WHITE;
    private int mTagKey;
    private Paint mPaint;
    private Paint mPaintLike;
    private Paint mPaintBrokenLine;
    private RectF rectFBg;
    private float loveSize = 0.8f;
    private float MaxSize = 1.2f;
    private String TAG = "HeartView";
    private Bitmap mBitmapBrokenLeftLove;
    private float mBrokenAngle = 13f;
    private Bitmap mBitmapBrokenRightLove;
    private float mAnimatedLikeValue = 0f;
    private float mAnimatedBrokenValue = 0f;


    private OnThumbUp onThumbUp;

    float startX = 0;
    float startY = 0;

    private WeakReference<View> heartView;
    private LikeType mUnLikeType = LikeType.broken;
    private ValueAnimator valueAnimator;
    private int mCounter;
    private boolean isReleased, isMoved;
    private Runnable mLongPressRunnable;
    private float TOUCH_SLOP = 20;
    private boolean isLongPressed;


    private HeartLayout heartLayout;

    public void setHeartLayout(HeartLayout heartLayout) {
        this.heartLayout = heartLayout;
    }

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public View getSearchView() {
        return heartView != null ? heartView.get() : null;
    }

    public void setSearchView(View view) {
        this.heartView = new WeakReference<View>(view);
    }


    public void setOnThumbUp(OnThumbUp onThumbUp) {
        this.onThumbUp = onThumbUp;
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ThumbUpView);
        if (typedArray != null) {
            edgeColor = typedArray.getColor(R.styleable.ThumbUpView_edgeColor, edgeColor);
            fillColor = typedArray.getColor(R.styleable.ThumbUpView_fillColor, fillColor);
            cracksColor = typedArray.getColor(R.styleable.ThumbUpView_cracksColor, cracksColor);
            int type = typedArray.getInteger(R.styleable.ThumbUpView_unlikeType, 0);
            if (type == 0) {
                mUnLikeType = LikeType.broken;

            } else {
                mUnLikeType = LikeType.unlike;
            }

            typedArray.recycle();
        }
        initPaint();
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {

                mCounter--;
                if(mCounter>0||isReleased||isMoved)
                    return;
                isLongPressed=true;
                if(heartLayout!=null){
                    heartLayout.autoLoop();
                }

            }
        };
    }

    private void initPaint() {
        mTagKey = getId();
        setSearchView(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintLike = new Paint();
        mPaintLike.setAntiAlias(true);
        mPaintLike.setStyle(Paint.Style.FILL);
        mPaintBrokenLine = new Paint();
        mPaintBrokenLine.setAntiAlias(true);
        mPaintBrokenLine.setStyle(Paint.Style.STROKE);
        rectFBg = new RectF();
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
            setMeasuredDimension(dip2px(130), dip2px(130));
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
        canvas.save();
        mPaint.setColor(edgeColor);
        mPaintLike.setColor(fillColor);
        mPaintBrokenLine.setColor(cracksColor);
        rectFBg = new RectF(0, 0,
                getMeasuredWidth(),
                getMeasuredHeight());

        rectFloveBg.top = rectFBg.centerY() - (rectFBg.height() / 2f) * loveSize;//* 0.5f;
        rectFloveBg.bottom = rectFBg.centerY() + (rectFBg.height() / 2f) * loveSize;// * 0.5f;
        rectFloveBg.left = rectFBg.centerX() - (rectFBg.width() / 2f) * loveSize;// * 0.5f;
        rectFloveBg.right = rectFBg.centerX() + (rectFBg.width() / 2f) * loveSize;//* 0.5f;

        mPaintLike.setStrokeWidth(rectFBg.width() / 20 + dip2px(1));//决定了爱心的大小
//        Log.e(TAG,"mPaintLike.setStrokeWidth->"+(rectFBg.width() / 20 + dip2px(1)));
        mPaint.setStrokeWidth(rectFBg.width() / 40);

        drawLove(canvas, mPaint, 1f, false);

        drawLove(canvas, mPaintLike, mAnimatedLikeValue, true);

        if (mAnimatedBrokenValue > 0 && mAnimatedBrokenValue < 0.5f) {
            float v = mAnimatedBrokenValue / 0.5f;
            drawBrokenLine(canvas, v);
        } else if (mAnimatedBrokenValue >= 0.5f && mAnimatedBrokenValue < 0.75f) {
            mAnimatedLikeValue = 0f;
            float v = (mAnimatedBrokenValue - 0.5f) / 0.25f;
            drawBrokenLove(canvas, mAnimatedBrokenValue);
        } else if (mAnimatedBrokenValue >= 0.75f && mAnimatedBrokenValue < 1f) {
            float v = (mAnimatedBrokenValue - 0.75f) / 0.25f;
            drawDrops(canvas, v);
        }
        canvas.restore();
    }

    public enum LikeType {
        broken, unlike, like
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            startX = x;
            startY = y;
            mCounter++;
            isReleased = false;
            isMoved = false;
            postDelayed(mLongPressRunnable, 1500);// 按下 3秒后调用线程
            return true;
        } else if (MotionEvent.ACTION_MOVE == event.getAction()) {
            if (isMoved)
                return false;
            if (Math.abs(startX - x) > TOUCH_SLOP
                    || Math.abs(startY - y) > TOUCH_SLOP) {
                // 移动超过阈值，则表示移动了
                isMoved = true;
                return false;
            }
        } else if (MotionEvent.ACTION_UP == event.getAction()) {
            isReleased = true;
            if(isLongPressed){
                Log.e(TAG,"长按结束了");
                isLongPressed=false;

                if (getSearchView().getTag(mTagKey) == null || !(Boolean) getSearchView().getTag(mTagKey)) {
                    startLikeAnim(LikeType.like);
                }

                if(heartLayout!=null){
                    heartLayout.stopLoop();
                }

                return  true;
            }

            if (Math.abs(event.getX() - startX) < 5 &&
                    Math.abs(event.getY() - startY) < 5) {
                if (rectFloveBg.contains(event.getX(), event.getY())) {
                    if (getSearchView().getTag(mTagKey) == null || !(Boolean) getSearchView().getTag(mTagKey)) {
                        startLikeAnim(LikeType.like);
                    } else if ((Boolean) getSearchView().getTag(mTagKey)) {
                        if (mUnLikeType == LikeType.broken) {
                            startLikeAnim(LikeType.broken);
                        } else if (mUnLikeType == LikeType.unlike) {
                            startLikeAnim(LikeType.unlike);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }




    private void startLikeAnim(LikeType like) {

        if (like == LikeType.unlike) {
            startViewAnim(0f, 1f, 200, like);
//            setTag(false,"tag");
            getSearchView().setTag(mTagKey, false);

        } else if (like == LikeType.like) {
            getSearchView().setTag(mTagKey, true);
            startViewAnim(0f, 1f, 200, like);

        } else if (like == LikeType.broken) {
            getSearchView().setTag(mTagKey, false);
            startViewAnim(0f, 1f, 400, like);
        }
        if (onThumbUp != null)
            onThumbUp.like((Boolean) getSearchView().getTag(mTagKey));

    }

    private ValueAnimator startViewAnim(float startF, float endF, int time, final LikeType like) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);//无限循环
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {


                if (like == LikeType.unlike) {
                    mAnimatedLikeValue = (float) valueAnimator.getAnimatedValue();
                    mAnimatedLikeValue = 1 - mAnimatedLikeValue;

                } else if (like == LikeType.like) {
                    mAnimatedLikeValue = (float) valueAnimator.getAnimatedValue();
                    mAnimatedLikeValue = mAnimatedLikeValue + (MaxSize - 1f);
                } else if (like == LikeType.broken) {
                    mAnimatedBrokenValue = (float) valueAnimator.getAnimatedValue();
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();

        }

        return valueAnimator;
    }


    private void drawLove(Canvas canvas, Paint mPaint, float mAnimatedValue, boolean fill) {
        if (mAnimatedValue - 1 > (MaxSize - 1) / 2f) {
            mAnimatedValue = 1 + (MaxSize - mAnimatedValue);
        }
        mAnimatedValue = mAnimatedValue * loveSize;

        RectF rectFlove = new RectF();
        rectFlove.top = rectFBg.centerY() - (rectFBg.height() / 2f + mPaint.getStrokeWidth()) * mAnimatedValue;//* 0.5f;
        rectFlove.bottom = rectFBg.centerY() + (rectFBg.height() / 2f + mPaint.getStrokeWidth()) * mAnimatedValue;// * 0.5f;
        rectFlove.left = rectFBg.centerX() - (rectFBg.width() / 2f + mPaint.getStrokeWidth()) * mAnimatedValue;// * 0.5f;
        rectFlove.right = rectFBg.centerX() + (rectFBg.width() / 2f + mPaint.getStrokeWidth()) * mAnimatedValue;//* 0.5f;

        float realWidth = rectFlove.width();
        float realHeight = rectFlove.height();
        rectFlove.top = rectFlove.top + realHeight * (1 - 0.8f) / 2f;
        Path path = new Path();

        float startYScale = 0;
        if (fill) {
            startYScale = 0.17f;
        } else {
            startYScale = 0.185f;
        }

        path.moveTo((float) (0.5 * realWidth) + rectFlove.left, (float) (startYScale * realHeight) + rectFlove.top);
        path.cubicTo((float) (0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight + rectFlove.top),
                (float) (-0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (realHeight * 0.8 + rectFlove.top));
//        path.moveTo( (float) (0.5 * realWidth) + rectFlove.left, (float) (realHeight * 0.8 + rectFlove.top));
        path.cubicTo((float) (realWidth + 0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (realWidth - 0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (startYScale * realHeight) + rectFlove.top);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    RectF rectFloveBg
            = new RectF();


    private void drawBrokenLine(Canvas canvas, float mAnimatedBrokenValue) {
        RectF rectFlove = new RectF();
        rectFlove.top = rectFBg.centerY() - (rectFBg.height() / 2f + mPaintLike.getStrokeWidth()) * loveSize;//* 0.5f;
        rectFlove.bottom = rectFBg.centerY() + (rectFBg.height() / 2f + mPaintLike.getStrokeWidth()) * loveSize;// * 0.5f;
        rectFlove.left = rectFBg.centerX() - (rectFBg.width() / 2f + mPaintLike.getStrokeWidth()) * loveSize;// * 0.5f;
        rectFlove.right = rectFBg.centerX() + (rectFBg.width() / 2f + mPaintLike.getStrokeWidth()) * loveSize;//* 0.5f;
        float realWidth = rectFlove.width();
        float realHeight = rectFlove.height();
        rectFlove.top = rectFlove.top + realHeight * (1 - 0.8f) / 2f;
        float fristX = (float) (0.5 * realWidth) + rectFlove.left;
        float fristY = (float) (0.17 * realHeight) + rectFlove.top;


        float lastX = (float) (0.5 * realWidth) + rectFlove.left;
        float lastY = (float) (realHeight * 0.8 + rectFlove.top);


        float secondX = lastX + realWidth / 14f;
        float secondY = fristY + (lastY - fristY) / 4f;


        float thirdX = lastX - realWidth / 12f;
        float thirdY = fristY + (lastY - fristY) / 2.5f;


        Path line = new Path();
        line.moveTo(fristX, fristY);

        if (mAnimatedBrokenValue > 0 && mAnimatedBrokenValue < 0.25f) {

            line.lineTo((secondX - fristX) * (mAnimatedBrokenValue / 0.25f) + fristX,
                    (secondY - fristY) * (mAnimatedBrokenValue / 0.25f) + fristY);
        }
        if (mAnimatedBrokenValue >= 0.25 && mAnimatedBrokenValue < 0.5f) {
            line.lineTo(secondX, secondY);
            line.lineTo((thirdX - secondX) * ((mAnimatedBrokenValue - 0.25f) / 0.25f) + secondX
                    , (thirdY - secondY) * ((mAnimatedBrokenValue - 0.25f) / 0.25f) + secondY);
        }
        if (mAnimatedBrokenValue >= 0.5 && mAnimatedBrokenValue <= 1f) {
            line.lineTo(secondX, secondY);
            line.lineTo(thirdX, thirdY);
            line.lineTo((lastX - thirdX) * ((mAnimatedBrokenValue - 0.5f) / 0.5f) + thirdX,
                    (lastY - thirdY) * ((mAnimatedBrokenValue - 0.5f) / 0.5f) + thirdY);
        }
        mPaintBrokenLine.setStrokeWidth(rectFlove.width() / 40);

        canvas.drawPath(line, mPaintBrokenLine);
    }


    private void drawBrokenLove(Canvas canvasMain, float mAnimatedBrokenValue) {
        Canvas canvas = null;
        RectF rectFlove = new RectF();
        rectFlove.top = rectFBg.centerY() - (rectFBg.height() / 2f + mPaintLike.getStrokeWidth()) * loveSize;//* 0.5f;
        rectFlove.bottom = rectFBg.centerY() + (rectFBg.height() / 2f + mPaintLike.getStrokeWidth()) * loveSize;// * 0.5f;
        rectFlove.left = rectFBg.centerX() - (rectFBg.width() / 2f + mPaintLike.getStrokeWidth()) * loveSize;// * 0.5f;
        rectFlove.right = rectFBg.centerX() + (rectFBg.width() / 2f + mPaintLike.getStrokeWidth()) * loveSize;//* 0.5f;
        float realWidth = rectFlove.width();
        float realHeight = rectFlove.height();
        rectFlove.top = rectFlove.top + realHeight * (1 - 0.8f) / 2f;
        float fristX = (float) (0.5 * realWidth) + rectFlove.left;
        float fristY = (float) (0.17 * realHeight) + rectFlove.top;
        float lastX = (float) (0.5 * realWidth) + rectFlove.left;
        float lastY = (float) (realHeight * 0.8 + rectFlove.top);
        float secondX = lastX + realWidth / 14f;
        float secondY = fristY + (lastY - fristY) / 4f;
        float thirdX = lastX - realWidth / 12f;
        float thirdY = fristY + (lastY - fristY) / 2.5f;

//        if (mBitmapBrokenLeftLove == null) {

        mBitmapBrokenLeftLove = Bitmap.createBitmap(getMeasuredWidth(), (int) lastY, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mBitmapBrokenLeftLove);
        canvas.rotate(-1 * mBrokenAngle * mAnimatedBrokenValue, lastX, lastY);

        Path path = new Path();
        path.moveTo((float) (0.5 * realWidth) + rectFlove.left, (float) (0.17 * realHeight) + rectFlove.top);
        path.cubicTo((float) (0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight + rectFlove.top),
                (float) (-0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (realHeight * 0.8 + rectFlove.top));
        path.lineTo(thirdX, thirdY);
        path.lineTo(secondX, secondY);
        path.close();
        canvas.drawPath(path, mPaintLike);
//        }

//        if (mBitmapBrokenRightLove == null) {
        mBitmapBrokenRightLove = Bitmap.createBitmap(getMeasuredWidth(), (int) lastY, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mBitmapBrokenRightLove);
        canvas.rotate(mBrokenAngle * mAnimatedBrokenValue, lastX, lastY);

//            Path path = new Path();
        path.reset();

        path.moveTo((float) (0.5 * realWidth) + rectFlove.left, (float) (realHeight * 0.8 + rectFlove.top));
        path.cubicTo((float) (realWidth + 0.4 * realWidth) + rectFlove.left, (float) (0.45 * realHeight) + rectFlove.top,
                (float) (realWidth - 0.15 * realWidth) + rectFlove.left, (float) (-0.35 * realHeight) + rectFlove.top,
                (float) (0.5 * realWidth) + rectFlove.left, (float) (0.17 * realHeight) + rectFlove.top);
        path.lineTo(secondX, secondY);
        path.lineTo(thirdX, thirdY);

        path.close();
        canvas.drawPath(path, mPaintLike);
//        }


        canvasMain.drawBitmap(mBitmapBrokenLeftLove
                , 0, 0, mPaint);
        canvasMain.drawBitmap(
                mBitmapBrokenRightLove
                , 0, 0, mPaint);
    }

    private void drawDrops(Canvas canvas, float mAnimatedBrokenValue) {
        if (mAnimatedBrokenValue == 1)
            return;

        RectF rectFlove = new RectF();
        rectFlove.top = rectFBg.centerY() -
                (rectFBg.height() / 2f + mPaintLike.getStrokeWidth()) * loveSize;//* 0.5f;
        rectFlove.bottom = rectFBg.centerY() + (rectFBg.height() / 2f +
                mPaintLike.getStrokeWidth()) * loveSize;// * 0.5f;
        rectFlove.left = rectFBg.centerX() - (rectFBg.width() / 2f +
                mPaintLike.getStrokeWidth()) * loveSize;// * 0.5f;
        rectFlove.right = rectFBg.centerX() + (rectFBg.width() / 2f +
                mPaintLike.getStrokeWidth()) * loveSize;//* 0.5f;


        canvas.drawCircle(rectFlove.centerX() -
                        rectFlove.width() / 4,
                rectFlove.centerY() + rectFlove.height() / 10 +
                        rectFlove.height() / 3 * mAnimatedBrokenValue,
                rectFlove.width() / 15 +
                        rectFlove.width() / 18 * (1 - mAnimatedBrokenValue),
                mPaintLike

        );
        canvas.drawCircle(rectFlove.centerX() +
                        rectFlove.width() / 4,
                rectFlove.centerY() + rectFlove.height() / 10
                        + rectFlove.height() / 3 * mAnimatedBrokenValue,
                rectFlove.width() / 15 +
                        rectFlove.width() / 18 * (1 - mAnimatedBrokenValue),
                mPaintLike

        );
    }
}
