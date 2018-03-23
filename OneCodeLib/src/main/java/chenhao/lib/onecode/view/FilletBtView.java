package chenhao.lib.onecode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import chenhao.lib.onecode.R;


public class FilletBtView extends TextView {

    private Paint paint;
    private boolean isPress;
    private float fillet=5,stroke=0;
    private OnClickListener clickListener;
    private OnTouchListener touchListener;
    private int filletColor,pressedColor,strokeColor;

    public FilletBtView(Context context) {
        super(context);
        init(null, 0);
    }

    public FilletBtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FilletBtView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        paint=new Paint();
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FilletStrokeView, defStyle, 0);
        fillet=a.getDimension(R.styleable.FilletStrokeView_fillet,fillet);
        stroke=a.getDimension(R.styleable.FilletStrokeView_stroke,stroke);
        filletColor=a.getColor(R.styleable.FilletStrokeView_filletColor, Color.BLACK);
        pressedColor=a.getColor(R.styleable.FilletStrokeView_pressedColor, Color.BLACK);
        strokeColor=a.getColor(R.styleable.FilletStrokeView_strokeColor, filletColor);
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setBgColor(int dColor,int pColor){
        this.filletColor=dColor;
        this.pressedColor=pColor;
        this.invalidate();
    }

    public void initColor(int dColor,int pColor,int sColor,int tColor){
        this.filletColor=dColor;
        this.pressedColor=pColor;
        this.strokeColor=sColor;
        this.setTextColor(tColor);
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
        drawFilletView(paint,canvas,getWidth(),getHeight(),fillet,stroke,isPress,filletColor,pressedColor,strokeColor);
        super.onDraw(canvas);
    }

    public static void drawFilletView(Paint paint,Canvas canvas,int width,int height,float fillet,float stroke,
                                      boolean isPress,int filletColor,int pressedColor,int strokeColor){
        if (null!=paint&&null!=canvas&&width>0&&height>0){
            try {
                paint.setAntiAlias(true);
                if (isPress){
                    paint.setColor(pressedColor);
                }else{
                    paint.setColor(filletColor);
                }
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(new RectF(0,0,width,height),fillet,fillet,paint);
                if (stroke>0){
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(stroke);
                    paint.setColor(strokeColor);
                    float sh=stroke/2;
                    canvas.drawRoundRect(new RectF(sh,sh,width-sh,height-sh),fillet,fillet,paint);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
