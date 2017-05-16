package chenhao.lib.onecode.image.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * @desc 阴影部分view
 */
public class ClipNewView extends View {
	
	private static final float DEFAULT_CORNER_THICKNESS_DP = 5;	//角的厚度(dp)
	private static final float DEFAULT_LINE_THICKNESS_DP = 3;	//边框的厚度(dp)
	private static final float DEFAULT_CORNER_LENGTH_DP = 20; //角线的显示长度(dp)
	private static final float DEFAULT_SHOW_GUIDELINES_MAX = 10;	//选择框最大时，距离屏幕的边距
	private static final float DEFAULT_SHOW_GUIDELINES_LIMIT = 100; //选择框最小剪裁区间(px)
	private static final float DEFAULT_GUIDELINE_THICKNESS_PX = 1; //网格线(px)
	
	private static final String DEFAULT_BACKGROUND_COLOR_ID = "#B0000000";	//阴影
	private static final String SEMI_TRANSPARENT = "#AAFFFFFF";
	
	private final int SHADOW_COLOR = 0x7f000000;
	private int mClipWidth = 100;
	private int mClipHeight = 100;
	private Bitmap mRectBitmap; // 用于背景缓存
	private Paint mBackgroundPaint;
	private Paint mBorderPaint;	// 边框
	private Paint mGuidelinePaint; //网格
	private Paint mCornerPaint;		//角
	private RectF clipRect;		//
	private RectF saveClipRect = new RectF();
	private PointF saveStart = new PointF();
	private RectF changeRect = new RectF();
	private boolean isFixed = false;
	private float scale = 0;
	
	
	public ClipNewView(Context context) {
		super(context);
		initPaint(context);
	}

	public ClipNewView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint(context);
	}

	public ClipNewView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint(context);
	}

	public void setFixed(boolean isHeader,float scale){
		this.isFixed = isHeader;
		this.scale = scale;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		initClipRect();
	}
	
	/**
	 * 初始化画笔
	 * @param context
	 */
	public void initPaint(Context context){
		 //边框
        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.parseColor(SEMI_TRANSPARENT));
        mBorderPaint.setStrokeWidth(getPxForDp(context, DEFAULT_LINE_THICKNESS_DP));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        
        //网络线
        mGuidelinePaint = new Paint();
        mGuidelinePaint.setColor(Color.parseColor(SEMI_TRANSPARENT));
        mGuidelinePaint.setStrokeWidth(DEFAULT_GUIDELINE_THICKNESS_PX);
        
        mBackgroundPaint =  new Paint();
        mBackgroundPaint.setColor(Color.parseColor(DEFAULT_BACKGROUND_COLOR_ID));
       
        mCornerPaint = new Paint();
        mCornerPaint.setColor(Color.WHITE);
        mCornerPaint.setStrokeWidth(getPxForDp(context, DEFAULT_CORNER_THICKNESS_DP));
        mCornerPaint.setStyle(Paint.Style.STROKE);

	}

	private float getPxForDp(Context context, float dp){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dp,context.getResources().getDisplayMetrics());
	}
	
	/**
	 * 画出阴影
	 * @param canvas
	 */
	 private void drawBackground(Canvas canvas) {
		 // Draw "top", "bottom", "left", then "right" quadrants.
		 if(clipRect!=null){
			 canvas.drawRect(0, 0, canvas.getWidth(), clipRect.top, mBackgroundPaint);
			 canvas.drawRect(0, clipRect.bottom, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
			 canvas.drawRect(0, clipRect.top, clipRect.left,clipRect.bottom, mBackgroundPaint);
			 canvas.drawRect(clipRect.right, clipRect.top, canvas.getWidth(), clipRect.bottom, mBackgroundPaint);
		 }
		 
	 }
	
	 /**
	  * 画出网络线
	  * @param canvas
	  */
	 private void drawRuleOfThirdsGuidelines(Canvas canvas) {

		 if(clipRect == null){
			 return;
		 }
		 
        // 画出两条竖线
        final float oneThirdCropWidth = clipRect.width() / 3;

        final float x1 = clipRect.left + oneThirdCropWidth;
        canvas.drawLine(x1, clipRect.top, x1, clipRect.bottom, mGuidelinePaint);
        final float x2 = clipRect.right - oneThirdCropWidth;
        canvas.drawLine(x2, clipRect.top, x2, clipRect.bottom, mGuidelinePaint);

        // 画出两条横线
        final float oneThirdCropHeight = clipRect.height() / 3;

        final float y1 = clipRect.top + oneThirdCropHeight;
        canvas.drawLine(clipRect.left, y1, clipRect.right, y1, mGuidelinePaint);
        final float y2 = clipRect.bottom - oneThirdCropHeight;
        canvas.drawLine(clipRect.left, y2, clipRect.right, y2, mGuidelinePaint);
    }

	 /**
	  * 画出国个角
	  * @param canvas
	  */
	private void drawCorners(Canvas canvas) {

		if(clipRect == null){
			return;
		}
		
		float corner_t = (getPxForDp(getContext(), DEFAULT_CORNER_THICKNESS_DP))/2;
		float corner_l = getPxForDp(getContext(), DEFAULT_CORNER_LENGTH_DP);
		
		final float left = clipRect.left-corner_t;
		final float right = clipRect.right+corner_t;
		final float top = clipRect.top-corner_t;
		final float bottom = clipRect.bottom+corner_t;
		
		
		// Top left
		canvas.drawLine(left, top-corner_t, left,top + corner_l, mCornerPaint);
		canvas.drawLine(left-corner_t, top, left + corner_l,top, mCornerPaint);

		// Top right
		canvas.drawLine(right, top-corner_t, right,top + corner_l, mCornerPaint);
		canvas.drawLine(right-corner_l, top, right+corner_t,top, mCornerPaint);

		// Bottom left
		canvas.drawLine(left, bottom-corner_l, left,bottom+corner_t, mCornerPaint);
		canvas.drawLine(left-corner_t, bottom, left + corner_l,bottom, mCornerPaint);

		// Bottom right
		canvas.drawLine(right, bottom - corner_l, right,bottom+corner_t, mCornerPaint);
		canvas.drawLine(right - corner_l, bottom, right+corner_t,bottom, mCornerPaint);

	}


	/**
	 * 获取截取区域位置信息
	 * 
	 * @return
	 */
	public RectF getClipRect() {
		return clipRect;
	}
	
	public void initClipRect(){
		clipRect = new RectF();
		int width = this.getWidth();
		int height = this.getHeight();
		if (mClipWidth != 0 && mClipHeight != 0) {
			int x = (width - mClipWidth) / 2;
			int y = (height - mClipHeight) / 2;
			// int y = 1;
			if (x > 0 && y > 0) {
				clipRect.set(x, y, x + mClipWidth, y + mClipHeight);
				saveClipRect.set(clipRect);
			} else {
				Log.e("ClipView", "Clip cal err");
			}
		}
	}
	

	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(clipRect == null){
			initClipRect();
			clipRect = getClipRect();
		}
		drawBackground(canvas);
		drawRuleOfThirdsGuidelines(canvas);
		drawCorners(canvas);
		float line_t = getPxForDp(getContext(), DEFAULT_LINE_THICKNESS_DP)/2;
		canvas.drawRect(clipRect.left-line_t,clipRect.top-line_t,clipRect.right+line_t,clipRect.bottom+line_t,mBorderPaint);
		
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	//是否显示网格，如果选择区太小时不显示网格
	private boolean showGuidelines(RectF clipRect) {
		if ((Math.abs(clipRect.width()) < DEFAULT_SHOW_GUIDELINES_LIMIT)
				|| (Math.abs(clipRect.height()) < DEFAULT_SHOW_GUIDELINES_LIMIT))
			return false;
		else
			return true;
	}

	@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRectBitmap != null) {
            mRectBitmap.recycle();
        }
    }

	/**
	 * 设置宽高
	 * 
	 * @param clipViewWidth
	 * @param clipViewHeight
	 */
	public void setSize(int clipViewWidth, int clipViewHeight) {
		mClipWidth = clipViewWidth;
		mClipHeight = clipViewHeight;
		initClipRect();
		invalidate();
	}
	
	/**
	 * 初始化点击位置
	 * @param start
	 */
	private void initStartPointF(PointF start) {
		if(saveStart.x!=start.x || saveStart.y!=start.y ){
			saveStart.set(start);
			saveClipRect.set(clipRect);
		}
	}

	
	
	/**
	 * 移动选择框 
	 * @param x
	 * @param y
	 * @param start
	 */
	public void dragClipView(float x, float y,PointF start){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left+(x-start.x), saveClipRect.top+(y-start.y),saveClipRect.right+(x-start.x), saveClipRect.bottom+(y-start.y));
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 左上角
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerLeftTop(float x, float y, PointF start, boolean isGeometric){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left+(x-start.x), saveClipRect.top+(isGeometric?(x-start.x)/scale:y-start.y),saveClipRect.right, saveClipRect.bottom);
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 左下角
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerLeftBottom(float x, float y, PointF start, boolean isGeometric){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left+(x-start.x), saveClipRect.top,saveClipRect.right, saveClipRect.bottom+(isGeometric?(start.x-x)/scale:y-start.y));
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 右上角
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerRightTop(float x, float y, PointF start, boolean isGeometric){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left, saveClipRect.top+(isGeometric?(start.x-x)/scale:y-start.y),saveClipRect.right+(x-start.x), saveClipRect.bottom);
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 右下角
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerRightBottom(float x, float y, PointF start, boolean isGeometric){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left, saveClipRect.top,saveClipRect.right+(x-start.x), saveClipRect.bottom+(isGeometric?(x-start.x)/scale:y-start.y));
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 上边
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerTop(float x, float y,PointF start){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left, saveClipRect.top+(y-start.y),saveClipRect.right, saveClipRect.bottom);
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 下边
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerBottom(float x, float y,PointF start){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left, saveClipRect.top,saveClipRect.right, saveClipRect.bottom+(y-start.y));
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 左边
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerLeft(float x, float y,PointF start){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left+(x-start.x), saveClipRect.top,saveClipRect.right, saveClipRect.bottom);
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	/**
	 * 右边
	 * @param x
	 * @param y
	 * @param start
	 */
	public void zoomCornerRight(float x, float y,PointF start){
		initStartPointF(start);
		if(saveClipRect!=null){
			changeRect.set(saveClipRect.left, saveClipRect.top,saveClipRect.right+(x-start.x), saveClipRect.bottom);
			if(isNotInSizeMin(changeRect)&& isNotMaxSize(changeRect)){
				clipRect.set(changeRect);
				invalidate();
			}
		}
	}
	
	/**
	 * 拉出图片边界后，如果是头像，则返回上一次剪裁区，如果剪裁范围大于图片，则以图片大小为剪裁区域
	 * @param imageRect
	 */
	public void recoverLast(RectF imageRect){
		if(imageRect.width()<clipRect.width() || imageRect.height()<clipRect.height()){
			if(clipRect.left<imageRect.left){
				clipRect.left = imageRect.left;
			}
			if(clipRect.top<imageRect.top){
				clipRect.top = imageRect.top;
			}
			if(clipRect.right>imageRect.right){
				clipRect.right = imageRect.right;
			}
			if(clipRect.bottom>imageRect.bottom){
				clipRect.bottom = imageRect.bottom;
			}
			
			if(isFixed){
				if(scale == 1){
					if(clipRect.width()<clipRect.height()){
						clipRect.bottom = clipRect.bottom-(clipRect.height()-clipRect.width());
					}else{
						clipRect.right = clipRect.right-(clipRect.width()-clipRect.height());
					}
				}else if(scale>0){
					clipRect.bottom = clipRect.bottom-(clipRect.height()-clipRect.width() /scale);
				}else{
					clipRect.right = clipRect.right-(clipRect.width() -clipRect.height() *scale);
				}
			}
			
		}else{
			if(saveClipRect!=null){
				clipRect.set(saveClipRect);
			}
		}
		invalidate();
	}
	
	/**
	 * 判断是否小于最少剪裁范围，如果太小则不再进行缩小
	 * @return
	 */
	public boolean isNotInSizeMin(RectF rect){
		return rect.right-rect.left>DEFAULT_SHOW_GUIDELINES_LIMIT && rect.bottom-rect.top>DEFAULT_SHOW_GUIDELINES_LIMIT;
	}
	
	public boolean isNotMaxSize(RectF rect){
		
		float max = getPxForDp(getContext(), DEFAULT_SHOW_GUIDELINES_MAX);
		if(rect.left<max){
			return false;
		}
		if(rect.top<max){
			return false;
		}
		if(rect.right>getWidth()-max){
			return false;
		}
		if(rect.bottom>getHeight()-max){
			return false;
		}
		return true;
	}
	
	
}
