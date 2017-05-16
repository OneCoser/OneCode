package chenhao.lib.onecode.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * 垂直滚动刻度尺
 */
public class RulerVerticalView extends RulerBaseView {

    public RulerVerticalView(Context context) {
        super(context);
    }

    public RulerVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerVerticalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        super.initVar();
        mRectHeight = (mMax - mMin) * mScaleMargin;
        mRectWidth = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;
        // 设置layoutParams
        ViewGroup.LayoutParams lp=getLayoutParams();
        if (null==lp){
            lp = new ViewGroup.MarginLayoutParams(mRectWidth, mRectHeight);
        }else{
            lp.width=mRectWidth;
            lp.height=mRectHeight;
        }
        this.setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.makeMeasureSpec(mRectWidth, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScaleScrollViewRange = getMeasuredHeight();
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, 0, 0, mRectHeight, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        int size=(int)(mRectWidth*0.22);
        paint.setTextSize(size);
        for (int i = 0, k = mMin; i <= mMax - mMin; i++) {
            if (i % mSection == 0) { //整值
                canvas.drawLine(0, i * mScaleMargin, mScaleMaxHeight, i * mScaleMargin, paint);
                //整值文字
                String kStr= String.valueOf(k);
                if (mPointCount>0){
                    double dk=k/(double)(10*mPointCount);
                    kStr= String.valueOf(dk);
                }
                canvas.drawText(kStr, mScaleMaxHeight + 40, i * mScaleMargin + paint.getTextSize() / 3, paint);
                k += mSection;
            } else {
                canvas.drawLine(0, i * mScaleMargin, mScaleHeight, i * mScaleMargin, paint);
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {
        paint.setColor(mPointerColor);
        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalY = mScroller.getFinalY();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalY / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { //回调方法
            double dk=mCountScale;
            if (mPointCount>0){
                dk=mCountScale/(double)(10*mPointCount);
            }
            mScrollListener.onScaleScroll(mCountScale,dk,mPointCount);
        }
        canvas.drawLine(0, countScale * mScaleMargin + finalY,
                mScaleMaxHeight + mScaleHeight, countScale * mScaleMargin + finalY, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataY = mScrollLastX - y;
                if (mCountScale - mTempScale < 0) { //向下边滑动
                    if (mCountScale <= mMin && dataY <= 0) //禁止继续向下滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { //向上边滑动
                    if (mCountScale >= mMax && dataY >= 0) //禁止继续向上滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(0, dataY);
                mScrollLastX = y;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCountScale < mMin) mCountScale = mMin;
                if (mCountScale > mMax) mCountScale = mMax;
                int finalY = (mCountScale - mMidCountScale) * mScaleMargin;
                mScroller.setFinalY(finalY); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
