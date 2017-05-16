package chenhao.lib.onecode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import chenhao.lib.onecode.R;

public class FilletLinearLayout extends LinearLayout {

    private boolean isPress;
    private float fillet=5,stroke=0;
    private OnClickListener clickListener;
    private OnTouchListener touchListener;
    private int filletColor,pressedColor,strokeColor;

    public FilletLinearLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public FilletLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FilletStrokeView, defStyle, 0);
        fillet=a.getDimension(R.styleable.FilletStrokeView_fillet,fillet);
        stroke=a.getDimension(R.styleable.FilletStrokeView_stroke,stroke);
        filletColor=a.getColor(R.styleable.FilletStrokeView_filletColor, Color.WHITE);
        pressedColor=a.getColor(R.styleable.FilletStrokeView_pressedColor, Color.WHITE);
        strokeColor=a.getColor(R.styleable.FilletStrokeView_strokeColor, filletColor);
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setBgColor(int dColor,int pColor){
        this.filletColor=dColor;
        this.pressedColor=pColor;
        this.invalidate();
    }

    public void initColor(int dColor,int pColor,int sColor){
        this.filletColor=dColor;
        this.pressedColor=pColor;
        this.strokeColor=sColor;
    }

    public void setFillet(float f){
        this.fillet=f;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.clickListener=l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        touchListener=l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null!=clickListener||null!=touchListener){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPress=true;
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    isPress=false;
                    this.invalidate();
                    if (event.getAction()== MotionEvent.ACTION_UP){
                        if (null!=clickListener){
                            clickListener.onClick(this);
                        }
                    }
                    break;
                default:
                    break;
            }
            if (null!=touchListener){
                return touchListener.onTouch(this,event);
            }else{
                return true;
            }
        }else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        if (isPress){
            paint.setColor(pressedColor);
        }else{
            paint.setColor(filletColor);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()),fillet,fillet,paint);
        if (stroke>0){
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(stroke);
            paint.setColor(strokeColor);
            canvas.drawRoundRect(new RectF(stroke/2,stroke/2,getWidth()-stroke,getHeight()-stroke),fillet,fillet,paint);
        }
        super.onDraw(canvas);
    }

}
