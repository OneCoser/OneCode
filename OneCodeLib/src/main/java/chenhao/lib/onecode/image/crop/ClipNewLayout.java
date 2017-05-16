package chenhao.lib.onecode.image.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import chenhao.lib.onecode.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chenhao.lib.onecode.image.ImageUtil;
import chenhao.lib.onecode.utils.StringUtils;

public class ClipNewLayout extends RelativeLayout implements OnTouchListener {
    private static final float MAX_SCALE = 10.0f;
    private static final int DEFAULT_SIZE = 1024;
    private static final int TARGET_RADIUS_DP = 24;        //手指点击时精度

    private ClipNewView mClipView;
    private ImageView mSouceImageView;

    private int mClipViewHeight = DEFAULT_SIZE;
    private int mClipViewWidth = DEFAULT_SIZE;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix mLastCurrentMatrix = new Matrix();
    private Matrix mBeforeTrackMatrix = new Matrix();

    private static final int MODE_NONE = 0;    //不处理
    private static final int MODE_IMAGE_DRAG = 1;    //图片移动
    private static final int MODE_IMAGE_ZOOM = 2;    //图片放大
    private static final int MODE_CLIP_ZOOM_LEFT = 3;
    private static final int MODE_CLIP_ZOOM_TOP = 4;
    private static final int MODE_CLIP_ZOOM_RIGHT = 5;
    private static final int MODE_CLIP_ZOOM_BOTTOM = 6;
    private static final int MODE_CLIP_ZOOM_LEFT_TOP = 7;
    private static final int MODE_CLIP_ZOOM_LEFT_BOTTOM = 8;
    private static final int MODE_CLIP_ZOOM_RIGHT_TOP = 9;
    private static final int MODE_CLIP_ZOOM_RIGHT_BOTTOM = 10;
    private static final int MODE_CLIP_DRAG = 11;    //裁剪框移动
    private int mode = MODE_NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private double oldDist = 1f;
//    private float oldRotation = 0;

    private DisplayMetrics dm;
    private Bitmap mBitmap, mSouceBitmap;
    private boolean mIsBeginTracking = false;
    private Window mWindow;
    private boolean isFixed = false;

    public ClipNewLayout(Context context) {
        super(context);
        init(context);
    }

    public ClipNewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClipNewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setInitData(float width, float height, boolean isFixed) {
        this.isFixed = isFixed;
        if (mClipView != null) {
            mClipView.setFixed(isFixed, width / height);
            if (width > 0 && height > 0) {
                if (width == height) {
                    mClipViewWidth = dm.widthPixels * 2 / 3;
                    mClipViewHeight = dm.widthPixels * 2 / 3;
                } else {
                    mClipViewWidth = dm.widthPixels * 2 / 3;
                    mClipViewHeight = (int) (mClipViewWidth * height / width);
                }
                mClipView.setSize(mClipViewWidth, mClipViewHeight);
            }
        }
    }

    private void init(Context context) {
        dm = context.getResources().getDisplayMetrics();

        LayoutInflater.from(getContext()).inflate(R.layout.onecode_clip_picture_layout_new,
                this);
        mClipView = (ClipNewView) findViewById(R.id.clipview);
        mSouceImageView = (ImageView) findViewById(R.id.src_pic);
        mSouceImageView.setOnTouchListener(this);
        mClipViewWidth = dm.widthPixels * 2 / 3;
        mClipViewHeight = dm.widthPixels * 2 / 3;
        mClipView.setSize(mClipViewWidth, mClipViewHeight);
    }

    /**
     * 以可视区域为中心进行旋转
     *
     * @param degree
     */
    public void rotate(float degree) {
        RectF rect = mClipView.getClipRect();
        matrix.postRotate(degree, (rect.left + rect.right) / 2,
                (rect.top + rect.bottom) / 2);
        mSouceImageView.setImageMatrix(matrix);
        mBeforeTrackMatrix.set(matrix);
        if (mClipView != null) {
            mClipView.recoverLast(getVisibleRect(matrix));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 用于修正某些手机响起全屏拍完照title会延迟显示
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != 0) {
            centerClip(mBitmap);
            mSouceImageView.setImageMatrix(matrix);
        }
    }

    /**
     * 设置要剪裁的图片
     * //    * @param bitmap
     *
     * @param imagePath
     * @param window
     */
    public void setSourceImage(String imagePath, Window window) {
        if (StringUtils.isEmpty(imagePath)) {
            return;
        }
        mSouceBitmap = ImageUtil.decodeFile(imagePath, dm.heightPixels * 2, true);
        if (mSouceBitmap == null) {
            return;
//            Toast.makeText(context)
        }
        int srcWidth = mSouceBitmap.getWidth();
        int srcHeight = mSouceBitmap.getHeight();
        float scaleWidth = (float) (mClipViewWidth) / (float) srcWidth;
        float scaleHeight = (float) (mClipViewHeight) / (float) srcHeight;
        float scale = Math.max(scaleWidth, scaleHeight);
        if (scale == 1) {
            mBitmap = mSouceBitmap;
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            mBitmap = Bitmap.createBitmap(mSouceBitmap, 0, 0, srcWidth, srcHeight,
                    matrix, true);
            mSouceImageView.setImageMatrix(matrix);
        }
        mSouceImageView.setImageBitmap(mBitmap);
        mWindow = window;
        centerClip(mBitmap);
    }

    public void onDestory() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mSouceBitmap != null && !mSouceBitmap.isRecycled()) {
            mSouceBitmap.recycle();
            mSouceBitmap = null;
        }
        if (mSouceImageView != null && mSouceImageView.getDrawable() != null && mSouceImageView.getDrawable() instanceof BitmapDrawable) {
            Bitmap viewBitmap = ((BitmapDrawable) mSouceImageView.getDrawable()).getBitmap();
            if (viewBitmap != null && !viewBitmap.isRecycled()) {
                viewBitmap.recycle();
            }

        }
        System.gc();
    }

    public boolean onTouch(View v, MotionEvent event) {
//    	Log.e("onTouch", "event.getAction():"+event.getAction()+"--mode:"+mode);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                mLastCurrentMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = getMode(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
//            oldRotation = rotation(event);
//            if (oldDist > 10f || oldRotation>1f) {
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = MODE_IMAGE_ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE_NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mClipView == null) {
                    break;
                }
//        	Log.e("MotionEvent.ACTION_MOVE", "mode="+mode+";x="+event.getX()+";y="+event.getY());
                if (mode == MODE_IMAGE_DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == MODE_IMAGE_ZOOM) {
                    double newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        double scale = newDist / oldDist;
//                    float rotation = rotation(event) - oldRotation;
                        matrix.postScale((float)scale, (float) scale, mid.x, mid.y);
//                    matrix.postRotate(rotation, mid.x, mid.y);//
                        if (!isMatrixOutFrame(matrix)) {
                            mLastCurrentMatrix.set(matrix);
                        }
                    }
                } else if (mode == MODE_CLIP_DRAG) {
                    mClipView.dragClipView(event.getX(), event.getY(), start);
                } else if (mode == MODE_CLIP_ZOOM_LEFT_TOP) {
                    mClipView.zoomCornerLeftTop(event.getX(), event.getY(), start, isFixed);
                } else if (mode == MODE_CLIP_ZOOM_LEFT_BOTTOM) {
                    mClipView.zoomCornerLeftBottom(event.getX(), event.getY(), start, isFixed);
                } else if (mode == MODE_CLIP_ZOOM_RIGHT_TOP) {
                    mClipView.zoomCornerRightTop(event.getX(), event.getY(), start, isFixed);
                } else if (mode == MODE_CLIP_ZOOM_RIGHT_BOTTOM) {
                    mClipView.zoomCornerRightBottom(event.getX(), event.getY(), start, isFixed);
                } else if (mode == MODE_CLIP_ZOOM_LEFT && !isFixed) {
                    mClipView.zoomCornerLeft(event.getX(), event.getY(), start);
                } else if (mode == MODE_CLIP_ZOOM_TOP && !isFixed) {
                    mClipView.zoomCornerTop(event.getX(), event.getY(), start);
                } else if (mode == MODE_CLIP_ZOOM_RIGHT && !isFixed) {
                    mClipView.zoomCornerRight(event.getX(), event.getY(), start);
                } else if (mode == MODE_CLIP_ZOOM_BOTTOM && !isFixed) {
                    mClipView.zoomCornerBottom(event.getX(), event.getY(), start);
                }
                break;
        }

        if (isMatrixOutFrame(matrix)) {
            mIsBeginTracking = true;
            Matrix m = new Matrix();
            m.set(matrix);
        } else {
            mIsBeginTracking = false;
            mBeforeTrackMatrix.set(savedMatrix);
        }

        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_POINTER_UP) {
            if (mIsBeginTracking) {
                mIsBeginTracking = false;
                if (isOutSizeWithImage(getVisibleRect(mLastCurrentMatrix), mClipView.getClipRect())) {
                    mClipView.recoverLast(getVisibleRect(mLastCurrentMatrix));
                } else {
                    animate(matrix, mLastCurrentMatrix);
                }
            } else if (isOutScale(matrix)) {
                if (isOutSizeWithImage(getVisibleRect(matrix), mClipView.getClipRect())) {
                    mClipView.recoverLast(getVisibleRect(mLastCurrentMatrix));
                } else {
                    animate(matrix, savedMatrix);
                }
            }
        } else {
            mSouceImageView.setImageMatrix(matrix);
        }

        return true;
    }


    /**
     * 剪裁框是否超出了图片范围,
     *
     * @param image
     * @param clip
     * @return
     */
    public boolean isOutSizeWithImage(RectF image, RectF clip) {
        return image.width() < clip.width() || image.height() < clip.height() || clip.left > image.right ||
                clip.top > image.bottom || clip.right < image.left || clip.bottom < image.top;
    }


    /**
     * 判断模式
     *
     * @param x
     * @param y
     * @return
     */
    private int getMode(float x, float y) {
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TARGET_RADIUS_DP, getContext().getResources().getDisplayMetrics());
        //左上角
        if (isInCornerTargetZone(x, y, mClipView.getClipRect().left, mClipView.getClipRect().top, radius)) {
            return MODE_CLIP_ZOOM_LEFT_TOP;
            //右上角
        } else if (isInCornerTargetZone(x, y, mClipView.getClipRect().right, mClipView.getClipRect().top, radius)) {
            return MODE_CLIP_ZOOM_RIGHT_TOP;
            //左下角
        } else if (isInCornerTargetZone(x, y, mClipView.getClipRect().left, mClipView.getClipRect().bottom, radius)) {
            return MODE_CLIP_ZOOM_LEFT_BOTTOM;
            //右下角
        } else if (isInCornerTargetZone(x, y, mClipView.getClipRect().right, mClipView.getClipRect().bottom, radius)) {
            return MODE_CLIP_ZOOM_RIGHT_BOTTOM;
            //上边
        } else if (isInHorizontalTargetZone(x, y, mClipView.getClipRect().left, mClipView.getClipRect().right, mClipView.getClipRect().top, radius)) {
            return MODE_CLIP_ZOOM_TOP;
            //下边
        } else if (isInHorizontalTargetZone(x, y, mClipView.getClipRect().left, mClipView.getClipRect().right, mClipView.getClipRect().bottom, radius)) {
            return MODE_CLIP_ZOOM_BOTTOM;
            //左边
        } else if (isInVerticalTargetZone(x, y, mClipView.getClipRect().top, mClipView.getClipRect().bottom, mClipView.getClipRect().left, radius)) {
            return MODE_CLIP_ZOOM_LEFT;
            //右边
        } else if (isInVerticalTargetZone(x, y, mClipView.getClipRect().top, mClipView.getClipRect().bottom, mClipView.getClipRect().right, radius)) {
            return MODE_CLIP_ZOOM_RIGHT;
            //选择中心
        } else if (isInCenterTargetZone(x, y, mClipView.getClipRect().left, mClipView.getClipRect().top, mClipView.getClipRect().right, mClipView.getClipRect().bottom)) {
            return MODE_CLIP_DRAG;
            //选择以外
        } else {
            return MODE_IMAGE_DRAG;
        }

    }

    /**
     * 判断是否按在上下边上
     *
     * @param x
     * @param y
     * @param xStart
     * @param xEnd
     * @param clipY
     * @param targetRadius
     * @return
     */
    private static boolean isInHorizontalTargetZone(float x, float y,
                                                    float xStart, float xEnd, float clipY,
                                                    float targetRadius) {

        if (x > xStart && x < xEnd && Math.abs(y - clipY) <= targetRadius) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否按在左右边上
     *
     * @param x
     * @param y
     * @param YStart
     * @param YEnd
     * @param clipX
     * @param targetRadius
     * @return
     */
    private static boolean isInVerticalTargetZone(float x, float y,
                                                  float YStart, float YEnd, float clipX,
                                                  float targetRadius) {

        if (Math.abs(x - clipX) <= targetRadius && y > YStart
                && y < YEnd) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否按在四个角上
     *
     * @param x            当前按下的点x座标
     * @param y            当前按下的点y座标
     * @param clipx        角的坐标x
     * @param clipY        角的坐标y
     * @param targetRadius 精度
     * @return
     */
    private static boolean isInCornerTargetZone(float x, float y,
                                                float clipx, float clipY, float targetRadius) {
        if (Math.abs(x - clipx) <= targetRadius
                && Math.abs(y - clipY) <= targetRadius) {
            return true;
        }
        return false;
    }

    /**
     * 是否在选择区以内
     *
     * @param x
     * @param y
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    private static boolean isInCenterTargetZone(float x, float y, float left,
                                                float top, float right, float bottom) {

        if (x > left && x < right && y > top && y < bottom) {
            return true;
        }
        return false;
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * Determine the space between the first two fingers
     */
    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
//        Log.e(event.getX(0) + "", event.getY(0) + "");
        point.set(x / 2, y / 2);
    }

    /**
     * 改变化矩阵是否超出框架
     *
     * @param matrix
     * @return
     */
    private boolean isMatrixOutFrame(final Matrix matrix) {
        RectF visibleRect = getVisibleRect(matrix);
        RectF snapRect = mClipView.getClipRect();
        return !visibleRect.contains(snapRect);
    }

    /* 获取矩形区域内的图片 */
    public Bitmap getBitmap() {
        Rect rect = getClipRect();
        if (rect == null) {
            return null;
        }
        float scale = 1;
        int maxEdge = Math.max(rect.width(), rect.height());
        if (maxEdge > DEFAULT_SIZE) {
            scale = (float) DEFAULT_SIZE / (float) maxEdge;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(getDegrees());
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(mSouceBitmap, rect.left,
                rect.top, rect.right - rect.left,
                rect.bottom - rect.top, matrix, false);
    }


    /**
     * 获取图片可视区域，原圖左上角为top，右下脚为bottom
     *
     * @return
     */
    private Rect getScaleRect() {
        Rect rect = mSouceImageView.getDrawable().getBounds();
        int width = rect.width();
        int height = rect.height();
        final Matrix matrix = mSouceImageView.getImageMatrix();
        float[] values = new float[9];
        matrix.getValues(values);

        Rect visibleRect = new Rect();
        width = (int) (width * values[8]);
        height = (int) (height * values[8]);
        visibleRect.left = (int) values[2];
        visibleRect.top = (int) values[5];
        visibleRect.right = (int) (visibleRect.left + width * values[0] + height
                * values[1]);
        visibleRect.bottom = (int) (visibleRect.top + height * values[0] - width
                * values[1]);

        return visibleRect;
    }

    /**
     * 获取截图后，针对于原始图片的区域
     *
     * @return
     */
    private Rect getClipRect() {

        if (mSouceBitmap == null) {
            return null;
        }
        Rect scale = getScaleRect();
        RectF insert = getInsertRect(mSouceImageView.getImageMatrix());
        int originalWidth = mSouceBitmap.getWidth();
        int originalHeight = mSouceBitmap.getHeight();
        Point top = getCloest(new Point(scale.left, scale.top), new Point(
                (int) insert.left, (int) insert.top), new Point(
                (int) insert.left, (int) insert.bottom), new Point(
                (int) insert.right, (int) insert.top), new Point(
                (int) insert.right, (int) insert.bottom));
        Point bottom = getFarthest(new Point(scale.left, scale.top), new Point(
                (int) insert.left, (int) insert.top), new Point(
                (int) insert.left, (int) insert.bottom), new Point(
                (int) insert.right, (int) insert.top), new Point(
                (int) insert.right, (int) insert.bottom));
        // TODO：可否考虑矩阵求逆（以后再讨论）
        int scaleWidth = Math.abs(scale.right - scale.left);
        int scaleHeight = Math.abs(scale.bottom - scale.top);
        float startXPercent = (float) Math.abs((top.x - scale.left))
                / (float) scaleWidth;
        float endXPercent = (float) Math.abs((bottom.x - scale.left))
                / (float) scaleWidth;
        float startYPercent = (float) Math.abs((top.y - scale.top))
                / (float) scaleHeight;
        float endYPercent = (float) Math.abs((bottom.y - scale.top))
                / (float) scaleHeight;
        int degrees = getDegrees();
        if (degrees == 90 || degrees == 270) {
            scaleWidth = Math.abs(scale.bottom - scale.top);
            scaleHeight = Math.abs(scale.right - scale.left);
            startXPercent = (float) Math.abs((top.y - scale.top))
                    / (float) scaleWidth;
            endXPercent = (float) Math.abs((bottom.y - scale.top))
                    / (float) scaleWidth;
            startYPercent = (float) Math.abs((top.x - scale.left))
                    / (float) scaleHeight;
            endYPercent = (float) Math.abs((bottom.x - scale.left))
                    / (float) scaleHeight;
        }

        Rect resultRec = new Rect((int) (originalWidth * startXPercent),
                (int) (originalHeight * startYPercent),
                (int) (originalWidth * endXPercent),
                (int) (originalHeight * endYPercent));
        return resultRec;
    }

    private Point getFarthest(final Point target, Point... points) {

        Comparator<Point> comparator = new Comparator<Point>() {

            @Override
            public int compare(Point o1, Point o2) {
                double p1_Top = Math.pow(o1.x - target.x, 2)
                        + Math.pow(o1.y - target.y, 2);
                double p2_Top = Math.pow(o2.x - target.x, 2)
                        + Math.pow(o2.y - target.y, 2);

                if (p1_Top < p2_Top)
                    return 1;
                else if (p1_Top == p2_Top)
                    return 0;
                else
                    return -1;
            }
        };

        List<Point> list = Arrays.asList(points);
        Collections.sort(list, comparator);
        return list.get(0);
    }

    private Point getCloest(final Point target, Point... points) {

        Comparator<Point> comparator = new Comparator<Point>() {

            @Override
            public int compare(Point o1, Point o2) {
                double p1_Top = Math.pow(o1.x - target.x, 2)
                        + Math.pow(o1.y - target.y, 2);
                double p2_Top = Math.pow(o2.x - target.x, 2)
                        + Math.pow(o2.y - target.y, 2);

                if (p1_Top < p2_Top)
                    return -1;
                else if (p1_Top == p2_Top)
                    return 0;
                else
                    return 1;
            }
        };

        List<Point> list = Arrays.asList(points);
        Collections.sort(list, comparator);
        return list.get(0);
    }

    private int getDegrees() {
        Rect scale = getScaleRect();
        int degrees = 0;
        if (scale.left > scale.right && scale.top < scale.bottom)
            degrees = 90;
        else if (scale.left > scale.right && scale.top > scale.bottom)
            degrees = 180;
        else if (scale.left < scale.right && scale.top > scale.bottom)
            degrees = 270;
        else
            degrees = 0;
        return degrees;
    }

    int statusBarHeight = 0;
    int titleBarHeight = 0;

    private void getBarHeight(Window window) {
        if (window == null) {
            return;
        }
        // 获取状态栏高度
        Rect frame = new Rect();
        View decorView = window.getDecorView();
        if (decorView == null) {
            return;
        }
        decorView.getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;

        int contenttop = window.findViewById(Window.ID_ANDROID_CONTENT)
                .getTop();
        // statusBarHeight是上面所求的状态栏的高度
        titleBarHeight = contenttop - statusBarHeight;

    }

    private boolean isOutScale(Matrix matrix) {
        boolean isOutMaxScale = false;
        float p[] = new float[9];
        matrix.getValues(p);
        float minScaleSize = Math.min(getVisibleRect(matrix).width(), getVisibleRect(matrix).height());
        if (minScaleSize > (getWidth() * MAX_SCALE)) {
            isOutMaxScale = true;
        }
        return isOutMaxScale;
    }

    private void scaleFit(Bitmap bitmap, float width, float height) {
        if (bitmap == null) {
            return;
        }
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float p[] = new float[9];
        matrix.getValues(p);
        float sourceHeight = bitmap.getWidth();
        float sourceWidth = bitmap.getHeight();
        float disHeight = sourceHeight - height;
        float disWidth = sourceWidth - width;
        if (sourceHeight > height && sourceWidth > sourceWidth) {
            if (disHeight > disWidth) {
                matrix.setScale(height / sourceHeight, height / sourceHeight);
            } else {
                matrix.setScale(width / sourceWidth, width / sourceWidth);
            }
        }
        if (sourceHeight < height && sourceWidth < width) {
            if (disHeight < disWidth) {
                matrix.setScale(height / sourceHeight, height / sourceHeight,
                        getWidth() / 2, 0);

            } else {
                matrix.setScale(width / sourceWidth, width / sourceWidth,
                        getWidth() / 2, 0);
            }
        }

    }

    private boolean isChange = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isChange) {
            isChange = false;
            scaleFit(mBitmap, mClipViewWidth, mClipViewHeight);
            mSouceImageView.setImageMatrix(matrix);
            centerClip(mBitmap);
            mSouceImageView.setImageMatrix(matrix);
        }
    }

    /**
     * 横向、纵向居中
     */
    private void centerClip(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        getBarHeight(mWindow);
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0;
        float deltaY = 0;
        int screenHeight = this.getHeight();
        if (height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < screenHeight) {
            deltaY = this.getHeight() - rect.bottom;
        }
        int screenWidth = dm.widthPixels;
        if (width < screenWidth) {
            deltaX = (screenWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < screenWidth) {
            deltaX = screenWidth - rect.right;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * @param fromMatrix
     * @param toMatrix
     */
    private void animate(Matrix fromMatrix, Matrix toMatrix) {
        RectF fromRect = getVisibleRect(fromMatrix);
        RectF toRect = getVisibleRect(toMatrix);
        boolean isTranslate = fromRect.width() == toRect.width();
        if (isTranslate) {
            fixMatrix(fromMatrix, toMatrix);
        }
        float[] fromValues = new float[9];
        fromMatrix.getValues(fromValues);
        int fromLeft = (int) fromValues[2];
        int fromTop = (int) fromValues[5];

        float[] toValues = new float[9];
        toMatrix.getValues(toValues);
        int toLeft = (int) toValues[2];
        int toTop = (int) toValues[5];

        int gap = 15;

        if (isTranslate) {
            // 只是坐标平移变换
            int maxTimes = 10;
            // int minDis=10;
            for (int i = 0; i < maxTimes; i++) {
                int dx = (toLeft - fromLeft) * (i + 1) / maxTimes;
                int dy = (toTop - fromTop) * (i + 1) / maxTimes;
                Matrix matrix = new Matrix(fromMatrix);
                matrix.postTranslate(dx, dy);
                Message msg = mRollbackHandler.obtainMessage();
                msg.what = 0;
                msg.obj = matrix;
                msg.arg1 = MSG_ANIM_ON;
                mRollbackHandler.sendMessageDelayed(msg, gap * i);
            }
            Message lastMsg = new Message();
            lastMsg.what = 0;
            lastMsg.obj = new Matrix(toMatrix);
            lastMsg.arg1 = MSG_ANIM_END;
            mRollbackHandler.sendMessageDelayed(lastMsg, gap * maxTimes);

        } else {
            // 只是拉伸变換
            int maxTimes = 10;
            // int minDis=10;
            float base = toRect.width() / fromRect.width();
            float delta = (float) Math.pow(base, (float) 1 / (float) maxTimes);
            Matrix matrix = new Matrix(fromMatrix);
            for (int i = 0; i < maxTimes; i++) {
                /*
                 * matrix.postScale(delta, delta, fromRect.left + fromRect.right
                 * / 2, toRect.top + toRect.bottom / 2);
                 */
                matrix.postScale(delta, delta, mid.x, mid.y);
                Matrix sendMatrix = new Matrix(matrix);
                Message msg = mRollbackHandler.obtainMessage();
                msg.what = 0;
                msg.obj = sendMatrix;
                msg.arg1 = MSG_ANIM_ON;
                mRollbackHandler.sendMessageDelayed(msg, gap * i);
            }
            Message lastMsg = mRollbackHandler.obtainMessage();
            lastMsg.what = 0;
            lastMsg.obj = new Matrix(toMatrix);
            lastMsg.arg1 = MSG_ANIM_END;
            mRollbackHandler.sendMessageDelayed(lastMsg, gap * maxTimes);

        }

    }

    /**
     * 返回交叉区域
     *
     * @return
     */
    private RectF getInsertRect(final Matrix matrix) {
        RectF visibleRect = getVisibleRect(matrix);
        RectF snapRect = mClipView.getClipRect();
        visibleRect.intersect(snapRect);
        return visibleRect;
    }

    // 平移回弹修正
    private void fixMatrix(Matrix fromMatrix, Matrix toMatrix) {
        RectF insert = getInsertRect(fromMatrix);
        RectF snapRect = mClipView.getClipRect();

        boolean hasInsert = insert.width() * insert.height() > 0;

        float[] fromValues = new float[9];
        fromMatrix.getValues(fromValues);
        float[] toValues = new float[9];
        toMatrix.getValues(toValues);

        int fromLeft = (int) fromValues[Matrix.MTRANS_X];
        int fromTop = (int) fromValues[Matrix.MTRANS_Y];
        int toLeft = fromLeft;
        int toTop = fromTop;

        if (isInside(snapRect, new PointF(insert.left, insert.top))) {
            toLeft += snapRect.left - insert.left;
            toTop += snapRect.top - insert.top;
        } else if (isInside(snapRect, new PointF(insert.right, insert.top))) {
            toLeft += snapRect.right - insert.right;
            toTop += snapRect.top - insert.top;

        } else if (isInside(snapRect, new PointF(insert.left, insert.bottom))) {
            toLeft += snapRect.left - insert.left;
            toTop += snapRect.bottom - insert.bottom;

        } else if (isInside(snapRect, new PointF(insert.right, insert.bottom))) {
            toLeft += snapRect.right - insert.right;
            toTop += snapRect.bottom - insert.bottom;
        } else if (insert.left > snapRect.left && insert.left <= snapRect.right
                && insert.top <= snapRect.top
                && insert.bottom >= snapRect.bottom) {
            toLeft += snapRect.left - insert.left;
        } else if (hasInsert && insert.right < snapRect.right
                && insert.right >= snapRect.left && insert.top <= snapRect.top
                && insert.bottom >= snapRect.bottom) {
            toLeft += snapRect.right - insert.right;
        } else if (hasInsert && insert.top > snapRect.top
                && insert.top <= snapRect.bottom
                && insert.left <= snapRect.left
                && insert.right >= snapRect.right) {
            toTop += snapRect.top - insert.top;
        } else if (hasInsert && insert.bottom < snapRect.bottom
                && insert.bottom >= snapRect.top
                && insert.left <= snapRect.left
                && insert.right >= snapRect.right) {
            toTop += snapRect.bottom - insert.bottom;
        } else {
            toLeft = (int) toValues[Matrix.MTRANS_X];
            toTop = (int) toValues[Matrix.MTRANS_Y];

        }
        toValues[Matrix.MTRANS_X] = toLeft;
        toValues[Matrix.MTRANS_Y] = toTop;
        toMatrix.setValues(toValues);
    }

    private boolean isInside(RectF rect, PointF point) {
        return point.x < rect.right && point.x > rect.left
                && point.y < rect.bottom && point.y > rect.top;
    }

    /**
     * 获取图片可视区域，屏幕左上角为top，右下脚为bottom
     *
     * @return
     */
    private RectF getVisibleRect(final Matrix matrix) {
        Drawable drawable = mSouceImageView.getDrawable();
        if (drawable == null) { // 可能为空，故加上null判断
            return new RectF(0, 0, 0, 0);
        }
        Rect rect = drawable.getBounds();
        int width = rect.width();
        int height = rect.height();
        // final Matrix matrix = mImageView.getImageMatrix();
        float[] values = new float[9];
        matrix.getValues(values);
        Rect visibleRect = new Rect();
        width = (int) (width * values[8]);
        height = (int) (height * values[8]);
        visibleRect.left = (int) values[2];
        visibleRect.top = (int) values[5];
        visibleRect.right = (int) (visibleRect.left + width * values[0] + height
                * values[1]);
        visibleRect.bottom = (int) (visibleRect.top + height * values[0] - width
                * values[1]);

        RectF newRect = new RectF();
        newRect.left = Math.min(visibleRect.left, visibleRect.right);
        newRect.top = Math.min(visibleRect.top, visibleRect.bottom);
        newRect.right = Math.max(visibleRect.left, visibleRect.right);
        newRect.bottom = Math.max(visibleRect.top, visibleRect.bottom);

        return newRect;
    }

    private static final int MSG_ANIM_ON = 0;
    private static final int MSG_ANIM_END = 1;

    private Handler mRollbackHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Matrix mmatrix = (Matrix) msg.obj;
            mSouceImageView.setImageMatrix(mmatrix);
            if (msg.arg1 == MSG_ANIM_END) {
                matrix.set(mmatrix);
                savedMatrix.set(mmatrix);
                mode = MODE_NONE;
            }
            return true;
        }
    });
}
