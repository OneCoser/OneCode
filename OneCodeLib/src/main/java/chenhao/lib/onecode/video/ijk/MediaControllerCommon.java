package chenhao.lib.onecode.video.ijk;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.utils.StringUtils;


public class MediaControllerCommon implements IMediaController {

    private OnControllerAVListener controllerAVListener;
    private MediaController.MediaPlayerControl mPlayer;
    private static final int SHOW_PROGRESS = 2;
    private boolean showIng, mDragging;

    private Context context;
    private SeekBar seekbar;
    private ImageView action,full;
    private View controllerView;
    private TextView current, total;

    public MediaControllerCommon(Context c, OnControllerAVListener listener) {
        this.context = c;
        this.controllerAVListener=listener;
        this.controllerView = View.inflate(c, R.layout.onecode_layout_common_video_play_control, null);
        action = (ImageView) controllerView.findViewById(R.id.common_video_action);
        full = (ImageView) controllerView.findViewById(R.id.common_video_full);
        current = (TextView) controllerView.findViewById(R.id.common_video_current);
        total = (TextView) controllerView.findViewById(R.id.common_video_total);
        seekbar = (SeekBar) controllerView.findViewById(R.id.common_video_seekbar);
        seekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        controllerView.setOnClickListener(onClickListener);
        action.setOnClickListener(onClickListener);
        full.setOnClickListener(onClickListener);
        seekbar.setMax(1000);
    }

    public View getControllerView() {
        return controllerView;
    }

    public void setFullIcon(boolean isFull){
        full.setImageResource(isFull?R.drawable.onecode_video_full_back_icon : R.drawable.onecode_video_full_icon);
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                long duration = mPlayer.getDuration();
                long newposition = (duration * progress) / 1000L;
                if (null != mPlayer) {
                    mPlayer.seekTo((int) newposition);
                }
                current.setText(StringUtils.videoForTime((int) newposition));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.common_video_action){
                doPauseResume();
            }else if(v.getId()==R.id.common_video_full){
                if (null!=controllerAVListener){
                    controllerAVListener.onClickFull();
                }
            }
        }
    };

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (duration > 0) {
            long pos = 1000L * position / duration;
            seekbar.setProgress((int) pos);
        }
        int percent = mPlayer.getBufferPercentage();
        seekbar.setSecondaryProgress(percent * 10);
        total.setText(StringUtils.videoForTime(duration));
        current.setText(StringUtils.videoForTime(position));
        return position;
    }

    private void updatePausePlay() {
        action.setImageResource(isPlaying()?R.drawable.onecode_video_stop_small :R.drawable.onecode_video_start_small);
    }

    public void doPauseResume() {
        if (isPlaying()) {
            mPlayer.pause();
            if (null!=controllerAVListener){
                controllerAVListener.pauseOrStart(false);
            }
        } else {
            if (null!=mPlayer){
                mPlayer.start();
            }
            if (null!=controllerAVListener){
                controllerAVListener.pauseOrStart(true);
            }
        }
        updatePausePlay();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    updatePausePlay();
                    pos = setProgress();
                    if (!mDragging && showIng && isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    public void onChangeActionShow(boolean actionShow) {
        this.showIng = actionShow;
        mHandler.removeMessages(SHOW_PROGRESS);
        if (showIng) {
            setProgress();
            updatePausePlay();
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        } else {

        }
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        this.mPlayer = player;
        updatePausePlay();
        setProgress();
    }

    public boolean isPlaying(){
        return null!=mPlayer&&mPlayer.isPlaying();
    }

    @Override
    public boolean isShowing() {
        return showIng;
    }

    @Override
    public void hide() {
    }

    @Override
    public void setAnchorView(View view) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void show(int timeout) {
    }

    @Override
    public void show() {
    }

    @Override
    public void showOnce(View view) {
    }

    public interface OnControllerAVListener{
        void pauseOrStart(boolean isStart);
        void onClickFull();
    }

}
