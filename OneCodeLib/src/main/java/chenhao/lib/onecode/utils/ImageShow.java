package chenhao.lib.onecode.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import java.io.File;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import chenhao.lib.onecode.view.LargeDraweeView;

public class ImageShow {

    public static void init(Context context){
        Fresco.initialize(context);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs()
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    public static int getDefaultImageLoadResId(){
        int id=R.drawable.default_image_load;
        if (null!=OneCode.getConfig()&&OneCode.getConfig().getDefaultImageLoadResId()!=0){
            id=OneCode.getConfig().getDefaultImageLoadResId();
        }
        return id;
    }

    public static int getDefaultHeadLoadResId(){
        int id=R.drawable.default_head_load;
        if (null!=OneCode.getConfig()&&OneCode.getConfig().getDefaultHeadLoadResId()!=0){
            id=OneCode.getConfig().getDefaultHeadLoadResId();
        }
        return id;
    }

    public static final String NULL_URI="chenhao.lib.onecode.ImageShow";

    public static String getResUri(int resId){
        if (resId>0){
            return "res:///"+resId;
        }else{
            return NULL_URI;
        }
    }
    public static String getDrawUri(int resId){
        if (resId>0){
            return "drawable://"+resId;
        }else{
            return NULL_URI;
        }
    }
    public static String getFileUri(String filePath){
        if (StringUtils.isNotEmpty(filePath)){
            return "file://"+filePath;
        }else{
            return NULL_URI;
        }
    }

    public static void loadRes(SimpleDraweeView view, int resId) {
        load(view, getResUri(resId),null);
    }
    public static void loadResImage(SimpleDraweeView view, int resId) {
        loadImage(view, getResUri(resId));
    }

    public static void loadFile(SimpleDraweeView view, String filePath) {
        load(view, getFileUri(filePath),null);
    }
    public static void loadFileImage(SimpleDraweeView view, String filePath) {
        loadImage(view, getFileUri(filePath));
    }

    public static void loadHead(SimpleDraweeView view, String uri) {
        setHierarchyHead(view);
        load(view,uri,null);
    }

    public static void loadImage(SimpleDraweeView view, String uri) {
        loadImage(view, uri, null);
    }

    public static void loadImage(SimpleDraweeView view, String uri, BasePostprocessor postprocessor) {
        setHierarchyDefault(view);
        load(view,uri,postprocessor);
    }

    public static void loadGif(SimpleDraweeView view, String uri) {
        loadGif(view, uri, true, null);
    }

    public static void loadGif(SimpleDraweeView view, String uri, boolean autoPlay, BaseControllerListener<ImageInfo> controllerListener) {
        if (null != view) {
            try {
                if (StringUtils.isEmpty(uri)){
                    uri=NULL_URI;
                }
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setControllerListener(controllerListener)
                        .setAutoPlayAnimations(autoPlay)
                        .setUri(Uri.parse(uri))
                        .build();
                view.setController(controller);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void load(SimpleDraweeView view, String uri, BasePostprocessor postprocessor) {
        if (null != view) {
            try {
                if (StringUtils.isEmpty(uri)){
                    uri=NULL_URI;
                }
                if (null != postprocessor) {
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                            .setPostprocessor(postprocessor)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(view.getController())
                            .build();
                    view.setController(controller);
                } else {
                    view.setImageURI(Uri.parse(uri));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void loadLarge(LargeDraweeView largeDraweeView, String uri){
        loadLarge(largeDraweeView,uri,false);
    }

    public static void loadLarge(LargeDraweeView largeDraweeView, String uri, boolean autoPlay){
        loadLarge(largeDraweeView,uri,autoPlay, getDefaultHeadLoadResId());
    }

    public static void loadLarge(LargeDraweeView largeDraweeView, String uri, boolean autoPlay, int defaultResId){
        if (null!=largeDraweeView){
            if (StringUtils.isEmpty(uri)){
                uri=NULL_URI;
            }
            largeDraweeView.setAutoPlay(autoPlay);
            setHierarchy(largeDraweeView,defaultResId);
            largeDraweeView.setImageURI(Uri.parse(uri));
        }
    }

    public static void setHierarchy(SimpleDraweeView view, int resId) {
        if (null!=view&&resId!=0){
            setHierarchy(view, ScalingUtils.ScaleType.CENTER_INSIDE,
                    getDrawable(resId),
                    getDrawable(resId),
                    getDrawable(resId));
        }
    }

    public static void setHierarchyDefault(SimpleDraweeView view) {
        if (null!=view){
            int id=getDefaultImageLoadResId();
            setHierarchy(view, ScalingUtils.ScaleType.CENTER_CROP, getDrawable(id), getDrawable(id), getDrawable(id));
        }
    }

    public static void setHierarchyHead(SimpleDraweeView view) {
        if (null!=view){
            int id=getDefaultHeadLoadResId();
            setHierarchy(view, ScalingUtils.ScaleType.FIT_CENTER, getDrawable(id), getDrawable(id), getDrawable(id));
        }
    }

    public static void setHierarchy(SimpleDraweeView view, ScalingUtils.ScaleType scaleType, Drawable placeholderDrawable, Drawable failureDrawable, Drawable retryDrawable) {
        GenericDraweeHierarchy hierarchy;
        if (view.hasHierarchy()) {
            hierarchy = view.getHierarchy();
            hierarchy.setPlaceholderImage(placeholderDrawable,scaleType);//加载中显示的图片
            hierarchy.setFailureImage(failureDrawable,scaleType);//加载失败时显示的图片
            hierarchy.setRetryImage(retryDrawable,scaleType);//重新加载时显示的图片
        } else {
            hierarchy = new GenericDraweeHierarchyBuilder(view.getResources())
                    .setPlaceholderImage(placeholderDrawable,scaleType)
                    .setFailureImage(failureDrawable,scaleType)
                    .setRetryImage(retryDrawable,scaleType)
                    .build();
            view.setHierarchy(hierarchy);
        }
    }

    public static Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(OneCode.getContext(),id);
    }

    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;
    private static int maxHeight = 0;
    // http://stackoverflow.com/questions/15313807/android-maximum-allowed-width-height-of-bitmap
    static {
        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        maxHeight = Math.max(maximumTextureSize, DEFAULT_MAX_BITMAP_DIMENSION);
    }

    public static int getMaxHeight(){
        return maxHeight;
    }




    /***********************
     * 华丽的分割线————下面是13图片加载框架的方法
     *
     *
     *  * switch (failReason.getType()) {
     * case IO_ERROR:
     * message = "图片加载失败";
     * break;
     * case DECODING_ERROR:
     * message = "图片加载失败";
     * break;
     * case NETWORK_DENIED:
     * message = "下载失败";
     * break;
     * case OUT_OF_MEMORY:
     * message = "内存空间不足";
     * break;
     * case UNKNOWN:
     * message = "未知错误";
     * break;
     * }
     * *****************************/

    public static void loadHead(ImageView view, String url) {
        load(view,url, getHeadOption(), null);
    }

    public static void loadRes(ImageView view, int rid) {
        loadImage(view,getDrawUri(rid));
    }

    public static void loadFile(ImageView view, String file) {
        loadImage(view, getFileUri(file));
    }

    public static void loadImage(ImageView view, String url) {
        load(view,url, getImgOption(), null);
    }

    public static void load(ImageView view, String url, DisplayImageOptions option, ImageLoadingListener listener) {
        if (StringUtils.isEmpty(url)){
            url=NULL_URI;
        }
        if (null != view) {
            ImageLoader.getInstance().displayImage(url, view, option, listener);
        } else if (null != listener) {
            ImageLoader.getInstance().loadImage(url, option, listener);
        }
    }


    public static boolean hasCatchFile13(String url) {
        File file = DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
        return file != null && file.exists() && file.length() > 0L;
    }

    public static File getCatchFile13(String url) {
        return DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
    }

    public static void clean13() {
        ImageLoader.getInstance().destroy();
    }

    private static DisplayImageOptions loadOption;

    public static DisplayImageOptions getLoadOption() {
        if (loadOption == null) {
            loadOption = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        }
        return loadOption;
    }

    private static DisplayImageOptions imgOption;

    public static DisplayImageOptions getImgOption() {
        if (imgOption == null) {
            int id=getDefaultImageLoadResId();
            imgOption = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(id)
                    .showImageOnFail(id)
                    .showImageOnLoading(id)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        }
        return imgOption;
    }

    private static DisplayImageOptions headOption;

    public static DisplayImageOptions getHeadOption() {
        if (headOption == null) {
            int id=getDefaultHeadLoadResId();
            headOption = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(id)
                    .showImageOnFail(id)
                    .showImageOnLoading(id)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        }
        return headOption;
    }


}
