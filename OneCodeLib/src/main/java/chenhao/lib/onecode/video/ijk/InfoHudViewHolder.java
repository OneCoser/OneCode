package chenhao.lib.onecode.video.ijk;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

public class InfoHudViewHolder {

    private IMediaPlayer mMediaPlayer;
    private TextView showInfo;

    public InfoHudViewHolder(TextView showInfo) {
        this.showInfo=showInfo;
    }

    public void setMediaPlayer(IMediaPlayer mp) {
        mMediaPlayer = mp;
        if (null!=showInfo&&mMediaPlayer != null) {
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
        } else {
            mHandler.removeMessages(MSG_UPDATE_HUD);
        }
    }

    private static String formatedDurationMilli(long duration) {
        if (duration >=  1000) {
            return String.format(Locale.US, "%.2f sec", ((float)duration) / 1000);
        } else {
            return String.format(Locale.US, "%d msec", duration);
        }
    }

    private static String formatedSize(long bytes) {
        if (bytes >= 100 * 1000) {
            return String.format(Locale.US, "%.2f MB", ((float)bytes) / 1000 / 1000);
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f KB", ((float)bytes) / 1000);
        } else {
            return String.format(Locale.US, "%d B", bytes);
        }
    }

    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_HUD: {
                    mHandler.removeMessages(MSG_UPDATE_HUD);
                    if (null!=showInfo){
                        IjkMediaPlayer mp = null;
                        if (mMediaPlayer == null)
                            break;
                        if (mMediaPlayer instanceof IjkMediaPlayer) {
                            mp = (IjkMediaPlayer) mMediaPlayer;
                        } else if (mMediaPlayer instanceof MediaPlayerProxy) {
                            MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
                            IMediaPlayer internal = proxy.getInternalMediaPlayer();
                            if (internal != null && internal instanceof IjkMediaPlayer)
                                mp = (IjkMediaPlayer) internal;
                        }
                        if (mp == null)
                            break;

                        int vdec = mp.getVideoDecoder();
                        StringBuilder builder=new StringBuilder();
                        builder.append("vdec：");
                        switch (vdec) {
                            case IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC:
                                builder.append("avcodec");
                                break;
                            case IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC:
                                builder.append("MediaCodec");
                                break;
                            default:
                                builder.append("null");
                                break;
                        }
                        float fpsOutput = mp.getVideoOutputFramesPerSecond();
                        float fpsDecode = mp.getVideoDecodeFramesPerSecond();
                        builder.append("\nfps：");
                        builder.append(String.format(Locale.US, "%.2f / %.2f", fpsDecode, fpsOutput));
                        long videoCachedDuration = mp.getVideoCachedDuration();
                        long audioCachedDuration = mp.getAudioCachedDuration();
                        long videoCachedBytes    = mp.getVideoCachedBytes();
                        long audioCachedBytes    = mp.getAudioCachedBytes();
                        builder.append("\nv-cache：");
                        builder.append(String.format(Locale.US, "%s, %s", formatedDurationMilli(videoCachedDuration), formatedSize(videoCachedBytes)));
                        builder.append("\na-cache：");
                        builder.append(String.format(Locale.US, "%s, %s", formatedDurationMilli(audioCachedDuration), formatedSize(audioCachedBytes)));
                        showInfo.setText(builder.toString());
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
                    }
                }
            }
        }
    };
}
