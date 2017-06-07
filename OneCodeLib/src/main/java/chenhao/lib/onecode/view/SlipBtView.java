package chenhao.lib.onecode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import chenhao.lib.onecode.R;

public class SlipBtView extends View {

    private float dp=0;
	private boolean IsOpen=false;
    private int btColor,closeColor,openColor;
    private SlipChangeListener changeListener;

    public SlipBtView(Context context) {
        super(context);
        init(null, 0);
    }

    public SlipBtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SlipBtView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        dp=getResources().getDisplayMetrics().density;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SlipBtView, defStyle, 0);
        if (null!=a){
            btColor=a.getColor(R.styleable.SlipBtView_sbv_btColor, Color.WHITE);
            closeColor=a.getColor(R.styleable.SlipBtView_sbv_closeColor, Color.parseColor("#666666"));
            openColor=a.getColor(R.styleable.SlipBtView_sbv_openColor, Color.BLACK);
            a.recycle();
        }
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setSlipChangeListener(SlipChangeListener l) {
        this.changeListener=l;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {

    }
    
    public boolean isOpen(){
        return IsOpen;
    }

    public void open(){
        if(null!=changeListener&&!IsOpen){
            changeListener.onChange(true,true);
        }
        IsOpen=true;
        this.invalidate();
    }

    public void close(){
        if(null!=changeListener&&IsOpen){
            changeListener.onChange(false,true);
        }
        IsOpen=false;
        this.invalidate();
    }

    private long downTime;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction())//根据动作来执行代码
        {
            case MotionEvent.ACTION_DOWN://按下
            		downTime= System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP://松开
                if (System.currentTimeMillis()-downTime<=800){
                		boolean LastChoose = IsOpen;
                    IsOpen=!LastChoose;
                    if(null!=changeListener){
                    		changeListener.onChange(IsOpen,false);
                    }
                }
                break;
            default:

        }
        invalidate();//重画控件
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        if (IsOpen){
            paint.setColor(openColor);
        }else{
            paint.setColor(closeColor);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), getHeight()/2, getHeight()/2, paint);
        int v1=(int)(5*dp);
        int v2=v1/2;
        int btWH=getHeight()-v1;
        float openL=getWidth()-btWH-v2;
        float openR=getWidth()-v2;
        float closeL=v2;
        float closeR=btWH+v2;
        float btL;
        float btR;
        if(IsOpen){
            btL=openL;
            btR=openR;
        }else{
            btL=closeL;
            btR=closeR;
        }
        if (btL<closeL||btR<closeR){
            btL=closeL;
            btR=closeR;
        }
        if (btL>openL||btR>openR){
            btL=openL;
            btR=openR;
        }
        paint.setColor(btColor);
        canvas.drawRoundRect(new RectF(btL,v2,btR,getHeight()-v2),btWH,btWH,paint);
        super.onDraw(canvas);
    }

    public interface SlipChangeListener{
        public void onChange(boolean isOpen, boolean isFromCode);
    }

}
