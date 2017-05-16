package chenhao.lib.onecode.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.base.BaseActivity;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.video.ijk.CommonVideoView;
import chenhao.lib.onecode.view.TitleView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by onecode on 16/5/3.
 * 视频播放
 */
public class SimlpVideoPlayActivity extends BaseActivity {

    @Override
    public int getStatusBarColor() {
        return Color.BLACK;
    }

    public static void play(Activity a, Video video) {
        if (null != a && null != video) {
            Intent intent = new Intent(a, SimlpVideoPlayActivity.class);
            intent.putExtra("video", video);
            a.startActivity(intent);
        }
    }

    private TitleView titleView;
    private CommonVideoView videoView;

    private Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onecode_activity_simlp_video_play);
        video =getIntent().getParcelableExtra("video");
        titleView=findV(R.id.title_view);
        videoView=findV(R.id.simlp_video_play);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action== TitleView.ACTION_LEFT_CLICK){
                    onBackPressed();
                }else if(action== TitleView.ACTION_RIGHT_CLICK){
                }
            }
        });
        changeFull(false);
        videoView.setVideoViewListener(videoViewListener);
        if(null!= video &&(StringUtils.isNotEmpty(video.url)|| StringUtils.isNotEmpty(video.path))){
            titleView.setTitle(StringUtils.isNotEmpty(video.name)? video.name:"视频播放");
            if (StringUtils.isNotEmpty(video.url)){
                videoView.setData(video.url, video.getShowUrl());
            }else{
                videoView.setData(video.path,"file://"+ video.thumbPath);
            }
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFull){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeFull(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private boolean isFull;
    public void changeFull(boolean full) {
        isFull = full;
        WindowManager.LayoutParams fullParams = getWindow().getAttributes();
        if (isFull) {
            fullParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(fullParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            fullParams.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(fullParams);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        videoView.setFullIcon(isFull);
    }

    private CommonVideoView.OnCommonVideoViewListener videoViewListener=new CommonVideoView.OnCommonVideoViewListener() {
        @Override
        public void onShowController(boolean show) {
            if (videoView.hasUrl()){
                setVis(titleView,show? View.VISIBLE: View.GONE);
            }
        }

        @Override
        public void pauseOrStart(boolean isStart) {

        }

        @Override
        public void onCompletion(IMediaPlayer mp) {

        }

        @Override
        public void onClickFull() {
            setRequestedOrientation(isFull ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stop();
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void reLoad(int status) {

    }
}
