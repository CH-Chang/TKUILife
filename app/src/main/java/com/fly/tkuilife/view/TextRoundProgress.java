package com.fly.tkuilife.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.fly.tkuilife.R;

public class TextRoundProgress extends View {
    private Paint paint;
    private int roundColor, progressColor, textColor, max, startAngle, progress;
    private float roundWidth, progressWidth, textSize, numSize;
    private boolean textShow;
    private String text;

    public TextRoundProgress(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TextRoundProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);

    }

    public TextRoundProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        paint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextRoundProgress);

        roundColor = typedArray.getColor(R.styleable.TextRoundProgress_roundColor, Color.rgb(112,112,112));
        roundWidth = typedArray.getDimension(R.styleable.TextRoundProgress_roundWidth, 5);
        progressColor = typedArray.getColor(R.styleable.TextRoundProgress_progressColor, Color.rgb(191,131,44));
        progressWidth= typedArray.getDimension(R.styleable.TextRoundProgress_progressWidth, 5);
        text = typedArray.getString(R.styleable.TextRoundProgress_text);
        textColor = typedArray.getColor(R.styleable.TextRoundProgress_textColor, Color.BLACK);
        textSize = typedArray.getDimension(R.styleable.TextRoundProgress_textSize, 15);
        numSize = typedArray.getDimension(R.styleable.TextRoundProgress_numSize, 15);
        max = typedArray.getInteger(R.styleable.TextRoundProgress_max, 0);
        progress = typedArray.getInteger(R.styleable.TextRoundProgress_progress, 0);
        startAngle = typedArray.getInt(R.styleable.TextRoundProgress_startAngle, 90);
        textShow = typedArray.getBoolean(R.styleable.TextRoundProgress_textShow, true);
        typedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int radius = (int) (centerX-roundWidth/2);


        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundColor);
        RectF round = new RectF(centerX, centerX, centerX, centerX);
        canvas.drawCircle( centerX, centerX, radius, paint);

        if(progress!=0){
            paint.setStrokeWidth(progressWidth);
            paint.setColor(progressColor);
            RectF oval = new RectF(centerX-radius, centerX-radius, centerX+radius, centerX+radius);
            int sweepAngle = 360 * progress / max ;
            canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
        }


        if(textShow&&text!=null&&text.length()>0){
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT);
            float textWidth = paint.measureText(text);
            canvas.drawText(text, centerX-textWidth/2, centerX+textSize+5, paint);

            paint.setTextSize(numSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            String situation = String.format("%02d", progress)+"/"+String.format("%02d", max);
            float numWidth = paint.measureText(situation);
            canvas.drawText(situation, centerX-numWidth/2, centerX, paint);



        }

    }

    public synchronized void setMax(int max){
        if(max<0) throw new IllegalArgumentException("Max not less than 0");
        else this.max = max;
    }

    public synchronized int getProgress(){
        return this.progress;
    }
    public synchronized int getMax(){
        return this.max;
    }

    public synchronized void setProgress(int progress){
        if(progress>max) throw new IllegalArgumentException("Max not less than progress");
        if(progress<0) throw new IllegalArgumentException("Progress not less than 0");
        else this.progress = progress;
        postInvalidate();
    }

    public synchronized void setText(String text){
        this.text = text;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);



        if(getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) setMeasuredDimension(widthSize, widthSize);
        else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) setMeasuredDimension(widthSize, heightMeasureSpec);
        else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) setMeasuredDimension(widthMeasureSpec, widthSize);
    }
}
