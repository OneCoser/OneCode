package chenhao.lib.onecode.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import chenhao.lib.onecode.R;
import chenhao.lib.onecode.view.aviloading.AVLoadingIndicatorView;

public class PullToRefreshView extends ViewGroup {

    private static final int DRAG_MAX_DISTANCE = 80;
    private static final float DRAG_RATE = .5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    public static final int MAX_OFFSET_ANIMATION_DURATION = 700;
    public static final int RESTORE_ANIMATION_DURATION = 2350;

    private static final int INVALID_POINTER = -1;

    private View mTarget;
    private RelativeLayout refreshHead;
    private ImageView refreshHeadIcon;
    private AVLoadingIndicatorView refreshHeadLoad;

    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
    private int mTotalDragDistance;
    private float mCurrentDragPercent;
    private int mCurrentOffsetTop;
    private boolean mRefreshing;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private int mFrom;
    private float mFromDragPercent;
    private boolean mNotify;
    private OnRefreshListener mListener;
    private int icon_refresh_down,icon_refresh_up;

    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = null;
        int loadColor= Color.parseColor("#999999");
        int loadStyle= AVLoadingIndicatorView.BallSpinFadeLoader;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.OneCodeRefreshView);
            icon_refresh_down=typedArray.getResourceId(R.styleable.OneCodeRefreshView_ptrv_icon_down,R.drawable.onecode_refresh_down_b);
            icon_refresh_up=typedArray.getResourceId(R.styleable.OneCodeRefreshView_ptrv_icon_up,R.drawable.onecode_refresh_up_b);
            loadColor=typedArray.getColor(R.styleable.OneCodeRefreshView_avlv_color,loadColor);
            loadStyle=typedArray.getColor(R.styleable.OneCodeRefreshView_avlv_style,loadStyle);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        float density = context.getResources().getDisplayMetrics().density;
        mTotalDragDistance = Math.round((float) DRAG_MAX_DISTANCE * density);

        refreshHead=new RelativeLayout(getContext());
        refreshHeadIcon=new ImageView(getContext());
        refreshHeadIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        refreshHeadLoad = new AVLoadingIndicatorView(this.getContext());
        refreshHeadLoad.setIndicatorColor(loadColor);
        refreshHeadLoad.setIndicatorId(loadStyle);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        refreshHead.addView(refreshHeadIcon,params);
        refreshHead.addView(refreshHeadLoad,params);
        addView(refreshHead);
        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        refreshStauts(STAUTS_DEFAULT);
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ensureTarget();
        if (mTarget == null)
            return;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        refreshHead.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != refreshHead)
                    mTarget = child;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {

        if (!isEnabled() || canChildScrollUp() || mRefreshing) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTop(0, true);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {

        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                final float scrollTop = yDiff * DRAG_RATE;
                mCurrentDragPercent = scrollTop / mTotalDragDistance;
                if (mCurrentDragPercent < 0) {
                    return false;
                }
                float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
                float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                float slingshotDist = mTotalDragDistance;
                float tensionSlingshotPercent = Math.max(0,
                        Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                        (tensionSlingshotPercent / 4), 2)) * 2f;
                float extraMove = (slingshotDist) * tensionPercent / 2;
                int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);

                setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overScrollTop > mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    mRefreshing = false;
                    refreshStauts(STAUTS_CANCEL);
                    animateOffsetToPosition(mAnimateToStartPosition);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    private void animateOffsetToPosition(Animation animation) {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;
        long animationDuration = (long) Math.abs(MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent);

        animation.reset();
        animation.setDuration(animationDuration);
        animation.setInterpolator(mDecelerateInterpolator);
        animation.setAnimationListener(mToStartListener);
        refreshHead.clearAnimation();
        refreshHead.startAnimation(animation);
    }

    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(RESTORE_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        refreshHead.clearAnimation();
        refreshHead.startAnimation(mAnimateToCorrectPosition);

        if (mRefreshing) {
            if (mNotify) {
                refreshStauts(STAUTS_START);
            }
        } else {
            refreshStauts(STAUTS_DEFAULT);
            animateOffsetToPosition(mAnimateToStartPosition);
        }
        mCurrentOffsetTop = mTarget.getTop();
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private Animation mAnimateToEndPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
            moveToEnd(interpolatedTime);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
            int targetTop;
            int endTarget = mTotalDragDistance;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();

            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;

            setTargetOffsetTop(offset, false /* requires update */);
        }

    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        mCurrentDragPercent = targetPercent;
        setTargetOffsetTop(offset, false);
    }

    private void moveToEnd(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f + interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        mCurrentDragPercent = targetPercent;
        setTargetOffsetTop(offset, false);
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition();
            } else {
                refreshStauts(STAUTS_STOP);
                animateOffsetToPosition(mAnimateToEndPosition);
            }
        }
    }

    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            refreshStauts(STAUTS_DEFAULT);
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        mTarget.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        changeHeadItemPostion();
        if (requiresUpdate&&mCurrentOffsetTop>getTotalDragDistance()){
            refreshStauts(STAUTS_CAN);
        }else if(requiresUpdate&&mCurrentOffsetTop>0){
            refreshStauts(STAUTS_CANCELING);
        }
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ensureTarget();
        if (mTarget == null)
            return;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
        refreshHead.layout(left, top, left + width - right, top + height - bottom);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
        void onRefreshStauts(int stauts);
    }

    private void changeHeadItemPostion(){
        RelativeLayout.LayoutParams iconParams=(RelativeLayout.LayoutParams)refreshHeadIcon.getLayoutParams();
        iconParams.topMargin=(mCurrentOffsetTop-refreshHeadIcon.getHeight())/2;
        if (iconParams.topMargin<0){
            iconParams.topMargin=0;
        }
        refreshHeadIcon.setLayoutParams(iconParams);
        RelativeLayout.LayoutParams loadParams=(RelativeLayout.LayoutParams)refreshHeadLoad.getLayoutParams();
        loadParams.topMargin=(mCurrentOffsetTop-refreshHeadLoad.getHeight())/2;
        if (loadParams.topMargin<0){
            loadParams.topMargin=0;
        }
        refreshHeadLoad.setLayoutParams(loadParams);
    }

    public static final int STAUTS_DEFAULT=0;
    public static final int STAUTS_CANCEL=1;
    public static final int STAUTS_CANCELING=2;
    public static final int STAUTS_CAN=3;
    public static final int STAUTS_START=4;
    public static final int STAUTS_STOP=5;
    private int iconId=0;
    private void refreshStauts(int stauts){
        setVis(refreshHead,stauts==STAUTS_DEFAULT?INVISIBLE:VISIBLE);
        switch (stauts){
            case STAUTS_DEFAULT://默认
            case STAUTS_CANCEL://刷新取消了
            case STAUTS_CANCELING://处于松手是取消的状态
                setRes(refreshHeadIcon,icon_refresh_down);
                setVis(refreshHeadLoad,GONE);
                setVis(refreshHeadIcon,VISIBLE);
                break;
            case STAUTS_CAN://手动下拉到可以刷新的状态了
            case STAUTS_STOP://结束刷新了
                setRes(refreshHeadIcon,icon_refresh_up);
                setVis(refreshHeadLoad,GONE);
                setVis(refreshHeadIcon,VISIBLE);
                break;
            case STAUTS_START://开始刷新了
                setVis(refreshHeadIcon,GONE);
                setVis(refreshHeadLoad,VISIBLE);
                if (mListener != null) {
                    mListener.onRefresh();
                }
                break;
        }
        if (mListener != null) {
            mListener.onRefreshStauts(stauts);
        }
    }

    public boolean isRefreshing(){
        return mRefreshing;
    }

    public void setHeadBackgroundView(View view){
        if (null!=view&&null!=refreshHead){
            refreshHead.addView(view,0);
        }
    }

    private void setRes(ImageView v, int id){
        if (null!=v&&iconId!=id){
            iconId=id;
            v.setImageResource(iconId);
        }
    }

    private void setVis(View v, int vis){
        if (null!=v&&v.getVisibility()!=vis){
            v.setVisibility(vis);
        }
    }

}
