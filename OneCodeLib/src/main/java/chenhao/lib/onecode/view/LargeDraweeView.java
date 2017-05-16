package chenhao.lib.onecode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import chenhao.lib.onecode.utils.Clog;
import chenhao.lib.onecode.utils.ImageShow;

/**
 * Created by onecode on 16/7/7.
 * 加载大图长图用
 */
public class LargeDraweeView extends SimpleDraweeView {
    private static final String TAG = "LargeDraweeView";

    private int WindowWidth;
    private boolean autoHeight,autoPlay;
    private Rect src;
    private Rect dst;
    private Paint paint;
    private ArrayList<Bitmap> bmps;

    public LargeDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        initDraweeView();
    }

    public LargeDraweeView(Context context) {
        super(context);
        initDraweeView();
    }

    public LargeDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDraweeView();
    }

    public LargeDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDraweeView();
    }

    public LargeDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initDraweeView();
    }

    public void initDraweeView() {
        WindowWidth = getResources().getDisplayMetrics().widthPixels;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        src = new Rect();
        dst = new Rect();
        autoHeight=false;
        autoPlay=false;
    }

    public void setAutoHeight(boolean isAutoHeight){
        this.autoHeight=isAutoHeight;
    }

    public void setAutoPlay(boolean isAutoPlay){
        this.autoPlay=isAutoPlay;
    }

    private void updateViewSize(ImageInfo imageInfo) {
        if (null!=imageInfo&&autoHeight){
            getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
        }
    }

    // getTimes(3, 4) == 0
    // getTimes(4, 4) == 1
    // getTimes(8, 4) == 2
    // getTimes(9, 4) == 3
    int getTimes(int actualNumber, int allowedMaxNumber){
        if(actualNumber < allowedMaxNumber)
            return 0;
        int result = actualNumber / allowedMaxNumber;
        if(result * allowedMaxNumber < actualNumber) {
            result += 1;
        }
        return result;
    }


    @Override
    public void setImageURI(Uri uri, Object callerContext) {
        Postprocessor postProcessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "SplitLongImagePostProcessor";
            }
            @Override
            public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
                CloseableReference<Bitmap> bitmapRef = null;
                try {
                    //1.当图片太宽了时重新计算一个合适的宽给图片: > windowWidth * 1.5
                    double ratio = 1.0;
                    if (sourceBitmap.getWidth() >= WindowWidth * 1.5) {
                        ratio = (double)WindowWidth / sourceBitmap.getWidth();
                    }
                    bitmapRef = bitmapFactory.createBitmap(
                            (int) (sourceBitmap.getWidth() * ratio),
                            (int) (sourceBitmap.getHeight() * ratio));

                    Bitmap destBitmap = bitmapRef.get();
                    Canvas canvas = new Canvas(destBitmap);
                    Rect destRect = new Rect(0, 0, destBitmap.getWidth(), destBitmap.getHeight());
                    canvas.drawBitmap(sourceBitmap, null, destRect, paint);
                    try {
                        //2.当图片太高了时分割图片再拼接显示: > OpenGL max Height
                        int imageTotalHeight = destBitmap.getHeight();
                        double imageAspectRatio = destBitmap.getWidth() / (double)WindowWidth;
                        int imageMaxAllowedHeight;
                        if(imageAspectRatio < 1) {
                            imageMaxAllowedHeight = (int) (ImageShow.getMaxHeight() * imageAspectRatio) - 5;
                        } else {
                            imageMaxAllowedHeight = ImageShow.getMaxHeight() - 5;
                        }
                        int imageCount = getTimes(imageTotalHeight, imageMaxAllowedHeight);
                        if (imageCount > 1) {
                            bmps = new ArrayList<Bitmap>();
                            Rect bsrc = new Rect();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            destBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(isBm, true);
                            for (int i = 0; i < imageCount; i++) {
                                bsrc.left = 0;
                                bsrc.top = i * imageMaxAllowedHeight;
                                bsrc.right = destBitmap.getWidth();
                                bsrc.bottom = Math.min(bsrc.top + imageMaxAllowedHeight, imageTotalHeight);
                                Bitmap bmp = decoder.decodeRegion(bsrc, null);
                                bmps.add(bmp);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                    return CloseableReference.cloneOrNull(bitmapRef);
                } finally {
                    CloseableReference.closeSafely(bitmapRef);
                }
            }
        };
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(postProcessor)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = ((PipelineDraweeControllerBuilder) getControllerBuilder())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        updateViewSize(imageInfo);
                    }
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        updateViewSize(imageInfo);
                    }
                })
                .setOldController(getController())
                .setCallerContext(callerContext)
                .setAutoPlayAnimations(autoPlay)
                .setImageRequest(request)
                .build();
        clearBitmaps();
        setController(controller);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bmps == null || bmps.size() <= 1) {
            super.onDraw(canvas);
        } else {
            Clog.i(TAG+"_onDrawSize："+bmps.size());
            int width=getWidth();
            int height=getHeight();
            int accumulatedHeight = 0;
            for (int i = 0; i < bmps.size(); i++) {
                Bitmap bmp = bmps.get(i);
                src.left = 0;
                src.top = 0;
                src.right = bmp.getWidth();
                src.bottom = bmp.getHeight();
                int srcCanDrawHeight=(int)((float)src.bottom / (float)src.right * width);
                dst.left = 0;
                dst.top = accumulatedHeight;
                dst.right = width;
                dst.bottom = accumulatedHeight + srcCanDrawHeight;
                canvas.drawBitmap(bmp, src, dst, paint);
                accumulatedHeight = dst.bottom;

                Clog.i(TAG+"_accumulatedHeight："+accumulatedHeight);
                if (!autoHeight&&accumulatedHeight>=height){
                    Clog.i(TAG+"_accumulatedHeight is break");
                    break;
                }
            }
        }
    }

    private void clearBitmaps(){
        if (null!=bmps&&bmps.size()>0){
            Clog.i(TAG+"_clearBitmaps："+bmps.size());
            try {
                for (Bitmap b:bmps){
                    b.recycle();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (null!=bmps){
                    bmps.clear();
                }
            }
        }
    }

}
