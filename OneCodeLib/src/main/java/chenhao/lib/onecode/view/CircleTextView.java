package chenhao.lib.onecode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class CircleTextView extends TextView {

    private int filletColor= Color.BLACK,strokeColor= Color.TRANSPARENT;
    private float stroke=0;
    private Paint paint;

    public CircleTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        paint=new Paint();
        TypedArray a = getContext().obtainStyledAttributes(attrs, chenhao.lib.onecode.R.styleable.FilletStrokeView, defStyle, 0);
        stroke=a.getDimension(chenhao.lib.onecode.R.styleable.FilletStrokeView_stroke,stroke);
        strokeColor=a.getColor(chenhao.lib.onecode.R.styleable.FilletStrokeView_strokeColor, Color.BLACK);
        filletColor=a.getColor(chenhao.lib.onecode.R.styleable.FilletStrokeView_filletColor, Color.BLACK);
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setColor(int fc,int sc){
        this.filletColor=fc;
        this.strokeColor=sc;
        this.invalidate();
    }

    public void setData(int filletColor,int strokeColor,float stroke,int textColor){
        this.filletColor=filletColor;
        this.strokeColor=strokeColor;
        this.stroke=stroke;
        setTextColor(textColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int helfW=getWidth()/2;
        int helfH=getHeight()/2;
        int max= Math.max(helfW,helfH);
        paint.setAntiAlias(true);
        if (stroke>0){
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            paint.setStrokeWidth(stroke);
            canvas.drawCircle(helfW,helfH,max-stroke/2,paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(filletColor);
            canvas.drawCircle(helfW,helfH,max-stroke+1,paint);
        }else{
            paint.setColor(filletColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(helfW,helfH,max,paint);
        }
        super.onDraw(canvas);
    }

}
