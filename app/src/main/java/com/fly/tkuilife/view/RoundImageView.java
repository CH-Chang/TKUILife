package com.fly.tkuilife.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.fly.tkuilife.R;

public class RoundImageView extends ImageView {

    private static final int MODE_NONE = 0;
    private static final int MODE_CIRCLE = 1;
    private static final int MODE_ROUND = 2;

    private Paint paint;
    private int mode;
    private float round;

    public RoundImageView(Context context) {
        super(context);
        obtainStyledAttrs(context, null, 0);
        initinstantate();
    }
    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttrs(context, attrs, 0);
        initinstantate();
    }
    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initinstantate();
        obtainStyledAttrs(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mode==MODE_CIRCLE){
            int radius = Math.min(getMeasuredHeight(), getMeasuredWidth());
            setMeasuredDimension(radius, radius);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable(); //取得圖片資源
        Matrix matrix = getImageMatrix(); //取得圖片縮放資訊

        if(drawable==null) return;
        if(drawable.getIntrinsicWidth()==0||drawable.getIntrinsicHeight()==0) return;
        if(matrix==null&&getPaddingTop()==0&&getPaddingLeft()==0) drawable.draw(canvas);
        else{
            int saveCount = canvas.getSaveCount();
            canvas.save();
            if(getCropToPadding()){
                int scrollX = getScrollX();
                int scrollY = getScrollY();
                canvas.clipRect(scrollX+getPaddingLeft(), scrollY+getPaddingTop(), scrollX+getRight()-getLeft()-getPaddingRight(), scrollY+getBottom()-getTop()-getPaddingBottom()); //裁剪畫布
            }
            canvas.translate(getPaddingLeft(), getPaddingTop());

            if(mode==MODE_NONE) {
                if (matrix!=null) canvas.concat(matrix);
                drawable.draw(canvas);
            }
            else if (mode==MODE_CIRCLE) {
                Bitmap bitmap = drawable2bitmap(drawable, matrix);
                paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, paint);
            }
            else if (mode==MODE_ROUND) {
                Bitmap bitmap = drawable2bitmap(drawable, matrix);
                paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawRoundRect(new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom()), round, round, paint);
            }
        }
    }

    private void obtainStyledAttrs(Context context, AttributeSet attrs, int defStyleRes){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleRes, 0);
        mode = typedArray.getInt(R.styleable.RoundImageView_mode, MODE_NONE);
        round = typedArray.getDimension(R.styleable.RoundImageView_radius, 0);
        typedArray.recycle();
    }
    private void initinstantate(){
        paint = new Paint();
    }
    private Bitmap drawable2bitmap(Drawable drawable, Matrix matrix){
        if(drawable==null) return null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if(matrix!=null) canvas.concat(matrix);
        drawable.draw(canvas);
        return bitmap;
    }
}
