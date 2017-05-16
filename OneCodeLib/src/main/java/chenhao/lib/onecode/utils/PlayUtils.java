package chenhao.lib.onecode.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PlayUtils implements OnCompletionListener {

    private static final String LOG_TAG = "PlayUtils";

    private PowerManager pm;
    private WakeLock m_wklk;
    private Context mContext;
    private String voiceUrl;
    private int voiceLength,playtimes;
    private MediaPlayer mPlayer = null;
    private CommentPlayListener listener;
    private Handler timeHandler = new Handler();

    public PlayUtils(Context mContext, CommentPlayListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");

    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    private Runnable theadTime = new Runnable() {
        @Override
        public void run() {
            playtimes--;
            if (playtimes>0){
                if (null!=listener){
                    listener.onPlayTime(playtimes);
                }
                timeHandler.postDelayed(theadTime,1000);
            }else{
                timeHandler.removeCallbacks(this);
            }
        }
    };

    /**
     * 开始播放
     */
    public void startPlaying(String filePath, int length) {
        this.voiceLength=length;
        this.voiceUrl=filePath;
        File file=new File(voiceUrl);
        if (null!=file&&file.exists()){
            try {
                m_wklk.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(this);
            muteAudioFocus(mContext, true);
            try {
                mPlayer.setDataSource((new FileInputStream(file)).getFD());
                mPlayer.prepare();
                mPlayer.start();
                if (listener != null) {
                    listener.onPlayStart();
                }
                if(voiceLength>0){
                    playtimes = voiceLength;
                    timeHandler.post(theadTime);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "prepare() failed");
            }
        }else if(null!=listener){
            listener.onPlayStop();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlaying() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        muteAudioFocus(mContext, false);
        timeHandler.removeCallbacks(theadTime);
        if (listener != null) {
            listener.onPlayStop();
        }
        mPlayer = null;
        try {
            m_wklk.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        try {
            if (mPlayer == null) {
                return false;
            } else {
                return mPlayer.isPlaying();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void destroy() {
        try {
            if (mPlayer != null) {
                stopPlaying();
                mPlayer.release();
                m_wklk.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }

    public interface CommentPlayListener {
        void onPlayTime(int time);
        void onPlayStart();
        void onPlayStop();
    }

    public static boolean muteAudioFocus(Context context, boolean bMute) {
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        Log.d("ANDROID_LAB", "pauseMusic bMute=" + bMute + " result=" + bool);
        return bool;
    }

}