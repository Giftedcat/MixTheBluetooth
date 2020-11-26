package com.hc.mixthebluetooth.customView;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.hc.mixthebluetooth.R;


/**
 * Created by xngly on 2020/5/7.
 */

public class CustomButtonView extends View{

    private int mRadius = 10;
    private int mChangeColor = Color.BLUE;
    private int mOriginal = Color.GRAY;
    private int mCircularColor = Color.BLACK;

    private int mStartX = -1;
    private int mStartY = -1;

    public enum State{Open,Close,Run};
    private State mState = State.Close;

    private Paint mCircularPain,mChangeLinePaint,mOriginalPaint;

    private float mNumber = 0;

    public CustomButtonView(Context context) {
        this(context,null);
    }

    public CustomButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomButtonView);
        mRadius = array.getDimensionPixelSize(R.styleable.CustomButtonView_CBRadius,mRadius);
        mChangeColor = array.getColor(R.styleable.CustomButtonView_CBChangeColor,mChangeColor);
        mOriginal = array.getColor(R.styleable.CustomButtonView_CBOriginalColor,mOriginal);
        mCircularColor = array.getColor(R.styleable.CustomButtonView_CBCircularColor,mCircularColor);
        array.recycle();
        mCircularPain = setCircularPaint();
        mChangeLinePaint = setLinePaint(true);
        mOriginalPaint = setLinePaint(false);
    }

    private Paint setCircularPaint(){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mCircularColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint setLinePaint(boolean b){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (b)
            paint.setColor(mChangeColor);
        else
            paint.setColor(mOriginal);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mRadius*2);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        drawLine(canvas);
        drawCircular(canvas);
    }

    private void init() {
        if (mStartX == -1)
            mStartX =  getPaddingLeft();
        if (mStartY == -1)
            mStartY =  getPaddingTop();
    }

    private void drawCircular(Canvas canvas) {
        if (mState == State.Close)
            canvas.drawCircle(mStartX+mRadius,mStartY+mRadius,mRadius,mCircularPain);
        else if (mState == State.Open)
            canvas.drawCircle(mStartX+4*mRadius,mStartY+mRadius,mRadius,mCircularPain);
        else if (mState == State.Run)
            canvas.drawCircle(mStartX+mRadius*mNumber,mStartY+mRadius,mRadius,mCircularPain);
    }

    private void drawLine(Canvas canvas) {
        if (mState == State.Close)
            canvas.drawLine(mStartX+mRadius,mStartY+mRadius,mStartX+4*mRadius,mStartY+mRadius,mOriginalPaint);
        else if (mState == State.Open)
            canvas.drawLine(mStartX+mRadius,mStartY+mRadius,mStartX+4*mRadius,mStartY+mRadius,mChangeLinePaint);
        else if(mState == State.Run){
            canvas.drawLine(mStartX+mRadius,mStartY+mRadius,mStartX+4*mRadius,mStartY+mRadius,mChangeLinePaint);
            canvas.drawLine(mStartX+mRadius*mNumber,mStartY+mRadius,mStartX+4*mRadius,mStartY+mRadius,mOriginalPaint);
        }
    }

    public void staysOn(){
        mState = State.Open;
        invalidate();
    }

    public void closed(){
        mState = State.Close;
        invalidate();
    }

    public void start(){
        mNumber = 0;
        mState = State.Run;
        ValueAnimator animator = ObjectAnimator.ofFloat(1,4);
        animator.setInterpolator(new MyInterpolator());
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mState == State.Run) {
                    mNumber = (float) animation.getAnimatedValue();
                    invalidate();
                }
            }
        });
        animator.start();
    }

    public void toggle(){
        mNumber = 0;
        ValueAnimator animator;
        final State state = mState;
        if (mState == State.Close)
            animator = ObjectAnimator.ofFloat(1,4);
        else if (mState == State.Open)
            animator = ObjectAnimator.ofFloat(4,1);
        else
            return;
        mState = State.Run;
        animator.setInterpolator(new MyInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mState == State.Run) {
                    mNumber = (float) animation.getAnimatedValue();
                    if (mNumber == 4 && state == State.Close){
                        mState = State.Open;
                    }
                    if (mNumber == 1 && state == State.Open){
                        mState = State.Close;
                    }

                    invalidate();
                }
            }
        });
        animator.start();
    }

    public boolean isChick(){
        return State.Open == mState;
    }


    public State getState(){
        return mState;
    }


    class  MyInterpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float input) {
            return input;
        }
    }
}
