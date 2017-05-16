package chenhao.lib.onecode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import chenhao.lib.onecode.R;

/**
 * Created by onecode on 16/3/18.
 */
public class LineProgressBar extends View {

    private float fillet,space;
    private long max,progress,secondary;
    private int backgroundColor,progressColor,secondaryColor;

    public LineProgressBar(Context context) {
        this(context,null);
    }

    public LineProgressBar(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LineProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    public void setMax(long m){
        this.max=m;
        invalidate();
    }

    public void setProgress(long p){
        if (p>max){
            p=max;
        }
        this.progress=p;
        invalidate();
    }

    public void setSecondary(long s){
        if (s<progress){
            s=progress;
        }
        if (s>max){
            s=max;
        }
        this.secondary=s;
        invalidate();
    }

    public void setColor(int b,int p,int s){
        this.backgroundColor=b;
        this.progressColor=p;
        this.secondaryColor=s;
        invalidate();
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LineProgressBar, defStyle, 0);
        fillet=a.getDimension(R.styleable.LineProgressBar_lpb_round,0);
        space=a.getDimension(R.styleable.LineProgressBar_lpb_space,0);
        backgroundColor=a.getColor(R.styleable.LineProgressBar_lpb_backColor, Color.parseColor("#aaaaaa"));
        progressColor=a.getColor(R.styleable.LineProgressBar_lpb_progColor, Color.parseColor("#56d2c2"));
        secondaryColor=a.getColor(R.styleable.LineProgressBar_lpb_secoColor, Color.TRANSPARENT);
        max=a.getInteger(R.styleable.LineProgressBar_lpb_max,100);
        progress=a.getInteger(R.styleable.LineProgressBar_lpb_pro,0);
        secondary=a.getInteger(R.styleable.LineProgressBar_lpb_sec,0);
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()),fillet,fillet,paint);
        float f=fillet-space;
        int psw=(int)(getWidth()-space*2);
        int psh=(int)(getHeight()-space*2);
        int pw=(int)(((double)progress/max)*psw);
        int sw=(int)(((double)secondary/max)*psw);
        paint.setColor(secondaryColor);
        canvas.drawRoundRect(new RectF(0,0,sw,psh),f,f,paint);
        paint.setColor(progressColor);
        canvas.drawRoundRect(new RectF(0,0,pw,psh),f,f,paint);
        super.onDraw(canvas);
    }
}
