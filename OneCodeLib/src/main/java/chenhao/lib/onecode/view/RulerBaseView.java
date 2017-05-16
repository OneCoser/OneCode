package chenhao.lib.onecode.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import chenhao.lib.onecode.R;

public abstract class RulerBaseView extends View {

    protected int mMin; // 最小刻度
    protected int mMax; //最大刻度
    protected int mSection; //按多少个分段
    protected int mPointCount; //小数点位数
    protected int mColor; //颜色
    protected int mPointerColor; //指标颜色
    protected int mCountScale; //滑动的总刻度

    protected int mScaleScrollViewRange;

    protected int mScaleMargin; //刻度间距
    protected int mScaleHeight; //刻度线的高度
    protected int mScaleMaxHeight; //整刻度线高度

    protected int mRectWidth; //总宽度
    protected int mRectHeight; //高度

    protected Scroller mScroller;
    protected int mScrollLastX;

    protected int mTempScale; // 用于判断滑动方向
    protected int mMidCountScale; //中间刻度

    protected OnScrollListener mScrollListener;

    public interface OnScrollListener {
        void onScaleScroll(int scale, double mathScale, int pointCount);
    }

    public RulerBaseView(Context context) {
        super(context);
        init(null,0);
    }

    public RulerBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public RulerBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerBaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,defStyleAttr);
    }

    public void setData(int start,int end,int section,int pointCount,int txtItemColor,int itemHeight,int itemSpace,int pointerColor){
        this.mMin=start;
        this.mMax=end;
        this.mSection=section;
        this.mPointCount=pointCount;
        this.mColor=txtItemColor;
        this.mScaleHeight=itemHeight;
        this.mScaleMargin=itemSpace;
        this.mPointerColor=pointerColor;
        initVar();
    }

    protected void init(AttributeSet attrs, int defStyle) {
        // 获取自定义属性
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RulerView, defStyle, 0);
        mMin = a.getInteger(R.styleable.RulerView_ruler_start,0);
        mMax = a.getInteger(R.styleable.RulerView_ruler_end,0);
        mPointCount = a.getInteger(R.styleable.RulerView_ruler_point_count,0);
        mSection = a.getInteger(R.styleable.RulerView_ruler_section,5);
        mScaleMargin=a.getDimensionPixelOffset(R.styleable.RulerView_ruler_item_space,10);
        mScaleHeight=a.getDimensionPixelOffset(R.styleable.RulerView_ruler_item_height,20);
        mColor=a.getColor(R.styleable.RulerView_ruler_color, Color.parseColor("#999999"));
        mPointerColor=a.getColor(R.styleable.RulerView_ruler_pointer_color, Color.RED);
        a.recycle();
        mScroller = new Scroller(getContext());
        initVar();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画笔
        Paint paint = new Paint();
        paint.setColor(mColor);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setDither(true);
        // 实心
        paint.setStyle(Paint.Style.FILL);
        // 文字居中
        paint.setTextAlign(Paint.Align.CENTER);

        onDrawLine(canvas, paint);
        onDrawScale(canvas, paint); //画刻度
        onDrawPointer(canvas, paint); //画指针

        super.onDraw(canvas);
    }

    protected void initVar(){
        if (mPointCount>2){
            mPointCount=2;
        }
    }

    // 画线
    protected abstract void onDrawLine(Canvas canvas, Paint paint);

    // 画刻度
    protected abstract void onDrawScale(Canvas canvas, Paint paint);

    // 画指针
    protected abstract void onDrawPointer(Canvas canvas, Paint paint);

    /**
     * 使用Scroller时需重写
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }
}
