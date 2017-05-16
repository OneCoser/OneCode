package chenhao.lib.onecode.video.ijk;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import chenhao.lib.onecode.R;

import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by onecode on 16/6/16.
 * 项目内通用的播放器  适用于普通一般的界面播放
 */
public class CommonVideoView extends RelativeLayout {

    public CommonVideoView(Context context) {
        super(context);
        init();
    }

    public CommonVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommonVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private float dp;
    private View controllerView;
    private IjkVideoView videoView;
    private MediaControllerCommon mediaControllerCommon;
    private OnCommonVideoViewListener videoViewListener;
    private SimpleDraweeView imageView;
    private ImageView startIcon;
    private String url,imageUrl;

    private void init(){
        dp=getResources().getDisplayMetrics().density;
        this.removeAllViews();
        this.setOnClickListener(rootOnClickListener);
        //添加播放器
        videoView=new IjkVideoView(getContext());
        videoView.setOnCompletionListener(onCompletionListener);
        LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(videoView,params);
        //添加控制器
        mediaControllerCommon =new MediaControllerCommon(getContext(),onControllerAVListener);
        videoView.setMediaController(mediaControllerCommon);
        controllerView= mediaControllerCommon.getControllerView();
        LayoutParams controllerViewParams=new LayoutParams(LayoutParams.MATCH_PARENT,(int)(30*dp));
        controllerViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        controllerView.setBackgroundColor(Color.parseColor("#99000000"));
        addView(controllerView,controllerViewParams);
        //添加封面图
        LayoutParams iconParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        iconParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView=new SimpleDraweeView(getContext());
        addView(imageView,iconParams);
        //添加大播放按钮
        startIcon=new ImageView(getContext());
        startIcon.setOnClickListener(startOnClickListener);
        startIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        startIcon.setImageResource(R.drawable.onecode_icon_play);
        addView(startIcon,iconParams);
    }

    public boolean hasUrl(){
        return StringUtils.isNotEmpty(url);
    }

    public void setData(String videoPath, String imagePath){
        if (!isPlaying()){
            imageView.setVisibility(VISIBLE);
            startIcon.setVisibility(VISIBLE);
        }
        showImage(imagePath);
        this.url=videoPath;
        actionShowController(false);
    }

    public void showImage(String path){
        this.imageUrl=path;
        ImageShow.setHierarchyDefault(imageView);
        if (null!=imageView.getHierarchy()){
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        }
        ImageShow.load(imageView,imageUrl,null);
    }

    public void play(String path){
        this.url=path;
        if (StringUtils.isNotEmpty(url)){
            videoView.setVideoPath(url);
            videoView.start();
            onControllerAVListener.pauseOrStart(true);
        }else{
            UiUtil.init().toast(getContext(),"播放地址错误");
        }
    }

    public boolean isPlaying(){
        return mediaControllerCommon.isPlaying();
    }

    public void pause(){
        if (isPlaying()){
            mediaControllerCommon.doPauseResume();
        }
    }

    public void stop(){
        boolean needEndNative=videoView.stopPlayback();
        videoView.release(true);
        videoView.stopBackgroundPlay();
        actionShowHandler.removeCallbacks(actionShowRunnable);
        if (needEndNative){
            IjkMediaPlayer.native_profileEnd();
        }
    }

    public void actionShowController(boolean show){
        actionShowHandler.removeCallbacks(actionShowRunnable);
        controllerView.setVisibility(show?VISIBLE:GONE);
        mediaControllerCommon.onChangeActionShow(show);
        if (null!=videoViewListener){
            videoViewListener.onShowController(show);
        }
        if (show){
            actionShowHandler.postDelayed(actionShowRunnable,5000);
        }
    }

    public void setFullIcon(boolean isFull){
        mediaControllerCommon.setFullIcon(isFull);
    }

    private Handler actionShowHandler = new Handler();
    private Runnable actionShowRunnable = new Runnable() {
        @Override
        public void run() {
            actionShowController(false);
        }
    };

    private OnClickListener rootOnClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            actionShowController(controllerView.getVisibility()==GONE);
        }
    };

    private OnClickListener startOnClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            play(url);
        }
    };

    private MediaControllerCommon.OnControllerAVListener onControllerAVListener=new MediaControllerCommon.OnControllerAVListener() {
        @Override
        public void pauseOrStart(boolean isStart) {
            if (isStart){
                startIcon.setVisibility(GONE);
                imageView.setVisibility(GONE);
                actionShowController(false);
            }
            if (null!=videoViewListener){
                videoViewListener.pauseOrStart(isStart);
            }
        }

        @Override
        public void onClickFull() {
            if (null!=videoViewListener){
                videoViewListener.onClickFull();
            }
        }
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener=new IMediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(IMediaPlayer mp) {
            actionShowController(true);
            if (null!=videoViewListener){
                videoViewListener.onCompletion(mp);
            }
        }
    };

    public void setVideoViewListener(OnCommonVideoViewListener listener) {
        this.videoViewListener = listener;
    }

    public interface OnCommonVideoViewListener{
        void onShowController(boolean show);
        void pauseOrStart(boolean isStart);
        void onCompletion(IMediaPlayer mp);
        void onClickFull();
    }

}
