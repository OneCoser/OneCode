package chenhao.lib.onecode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 水平滚动刻度尺
 */
public class RulerHorizontalView extends RulerBaseView {

    public RulerHorizontalView(Context context) {
        super(context);
    }

    public RulerHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RulerHorizontalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        super.initVar();
        mRectWidth = (mMax - mMin) * mScaleMargin;
        mRectHeight = mScaleHeight * 8;
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
        int height= View.MeasureSpec.makeMeasureSpec(mRectHeight, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
        mScaleScrollViewRange = getMeasuredWidth();
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, mRectHeight/2, mRectWidth, mRectHeight/2, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        int size=(int)(mRectHeight*0.22);
        paint.setTextSize(size);
        for (int i = 0, k = mMin; i <= mMax - mMin; i++) {
            if (i % mSection == 0) { //整值
                canvas.drawLine(i * mScaleMargin, mRectHeight/2, i * mScaleMargin, mRectHeight - mScaleMaxHeight-mRectHeight/2, paint);
                //整值文字
                String kStr= String.valueOf(k);
                if (mPointCount>0){
                    double dk=k/(double)(10*mPointCount);
                    kStr= String.valueOf(dk);
                }
                canvas.drawText(kStr, i * mScaleMargin, mRectHeight/2+5+size, paint);
                k += mSection;
            } else {
                canvas.drawLine(i * mScaleMargin, mRectHeight/2, i * mScaleMargin, mRectHeight - mScaleHeight-mRectHeight/2, paint);
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {
        paint.setColor(mPointerColor);
        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getFinalX();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalX / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { //回调方法
            double dk=mCountScale;
            if (mPointCount>0){
                dk=mCountScale/(double)(10*mPointCount);
            }
            mScrollListener.onScaleScroll(mCountScale,dk,mPointCount);
        }
        canvas.drawLine(countScale * mScaleMargin + finalX, mRectHeight/2,
                countScale * mScaleMargin + finalX, mRectHeight - mScaleMaxHeight - mScaleHeight-mRectHeight/2, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;
                if (mCountScale - mTempScale < 0) { //向右边滑动
                    if (mCountScale <= mMin && dataX <= 0) //禁止继续向右滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { //向左边滑动
                    if (mCountScale >= mMax && dataX >= 0) //禁止继续向左滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                checkData();
                return true;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    private void checkData(){
        if (mCountScale < mMin) mCountScale = mMin;
        if (mCountScale > mMax) mCountScale = mMax;
        int finalX = (mCountScale - mMidCountScale) * mScaleMargin;
        mScroller.setFinalX(finalX); //纠正指针位置
        postInvalidate();
    }

    public void setData(int data){
        mTempScale=mCountScale;
        mCountScale=data;
        checkData();
    }

}
