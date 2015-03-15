package com.goodcodeforfun.sunshine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class MyView extends View {
    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 //       super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int myHeight = hSpecSize;
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidth = wSpecSize;
        if (hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            myHeight = 120;
        }
        if (wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            myWidth = 120;
        }
        setMeasuredDimension(myWidth,myHeight);
    }


    private Paint mWindmillPaint;
    private float mSpeed;
    private float mRotation = 359f;
    private Bitmap mRotor;

    private void init() {
        mWindmillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWindmillPaint.setStyle(Paint.Style.FILL);
        mRotor = BitmapFactory.decodeResource(getResources(), R.drawable.rotor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw rotor
        int h = 0;
        int w = 0;
        canvas.drawBitmap(mRotor, rotate(mRotor, h, w), mWindmillPaint);
        invalidate();
    }

    public Matrix rotate(Bitmap bm, int x, int y){
        Matrix mtx = new Matrix();
        mtx.postRotate(mRotation, bm.getWidth() / 2, bm.getHeight() / 2);
        mtx.postTranslate(x, y);  //The coordinates where we want to put our bitmap
        mRotation -= mSpeed; //degree of rotation
        return mtx;
    }

    public void setSpeed(float speed) {
        mSpeed = speed*4; //Looks better like this...
        final AccessibilityManager am =
                (AccessibilityManager)(getContext()
                        .getSystemService(Context.ACCESSIBILITY_SERVICE));
        if (am.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.dispatchPopulateAccessibilityEvent(event);
        event.getText().add(String.valueOf(mSpeed));
        return true;
    }

}
