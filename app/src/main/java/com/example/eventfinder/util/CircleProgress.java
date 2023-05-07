package com.example.eventfinder.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircleProgress extends View {

    private static final String TAG = "MyProgress";


    private Paint _paint;
    private RectF _rectF;
    private Rect _rect;
    private int _current = 30, _max = 100;
    private float _arcWidth = 25;
    private float _width;
    private int alpha = 0;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _paint = new Paint();
        _paint.setAntiAlias(true);
        _rectF = new RectF();
        _rect = new Rect();
    }

    public void SetCurrent(int _current) {
        Log.i(TAG, "Current Value:" + _current + ", Max Value:" + _max);
        this._current = _current;
        invalidate();
    }

    public void SetMax(int _max) {
        this._max = _max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        _width = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setStrokeWidth(_arcWidth);
        _paint.setColor(Color.rgb(0, 0, 0));
        _paint.setStrokeCap(Paint.Cap.ROUND);
//        _paint.setShader(linearGradient);
//        _paint.setShadowLayer(10, 10, 10, Color.GRAY);

        float bigCircleRadius = _width / 2;
        float smallCircleRadius = bigCircleRadius - _arcWidth;
        canvas.drawCircle(bigCircleRadius, bigCircleRadius, smallCircleRadius, _paint);
        _paint.setColor(Color.rgb(140, 36, 60));
        //_paint.setAlpha((int) (alpha + ((float) _current / _max) * 280));
        _rectF.set(_arcWidth, _arcWidth, _width - _arcWidth, _width - _arcWidth);
        canvas.drawArc(_rectF, 270, _current * 360 / _max, false, _paint);

        //String txt = _current * 100 / _max + "%";
        String txt = String.valueOf(_current * 100 / _max);
        _paint.setStrokeWidth(3);
        _paint.setTextSize(40);
        _paint.getTextBounds(txt, 0, txt.length(), _rect);
        _paint.setColor(Color.rgb(255, 255, 255));
        canvas.drawText(txt, bigCircleRadius - _rect.width() / 2, bigCircleRadius + _rect.height() / 2, _paint);
    }

}