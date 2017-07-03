package chenhao.lib.onecode.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.io.File;
import chenhao.lib.onecode.view.AlertMsg;

public class RecordUtils {

    private Context mContext;
    public boolean isRecording = false;
    private MediaRecorder mRecorder = null;
    private PowerManager pm;
    private WakeLock m_wklk;
    private String filePath;
    private boolean isSuccess,isCancel;
    private RecordListener recordListener;

    public RecordUtils(Context context, RecordListener listener) {
        this.mContext = context;
        this.recordListener=listener;
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    Handler recordHandler = new Handler();
    private int recordTime = 0;
    private int max_time = 60000;

    public void setMaxTime(int time) {
        this.max_time = time;
    }

    Runnable recordThread = new Runnable() {
        @Override
        public void run() {
            if (recordTime <= max_time && isRecording) {
                recordTime+=1;
                if(null!=recordListener){
                    recordListener.onTimeAdd(recordTime);
                }
                recordHandler.postDelayed(recordThread, 1000);
            } else {
                stopRecord(isCancel);
            }
        }
    };

    public void startRecord(String savePath) {
        filePath=savePath;
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
        m_wklk.acquire();
        isRecording = true;
        recordTime = 0;
        startRecording();
        recordHandler.post(recordThread);
    }

    public void stopRecord(boolean cancel) {
        isRecording = false;
        isCancel=cancel;
        stopRecording();
        recordHandler.removeCallbacks(recordThread);
        if (null != m_wklk) {
            m_wklk.release();
            m_wklk = null;
        }
    }

    private void startRecording() {
        if (this.mRecorder == null) {
            isSuccess = true;
            mRecorder = new MediaRecorder();
            if (mContext.getPackageManager().checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()) == PackageManager.PERMISSION_DENIED) {
                isSuccess = false;
                this.stopRecord(true);
                new AlertMsg(mContext,null).setMsg("应用录音权限被禁用，请到设置里更改","确定","").createShow();
            } else {
                try {
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                    PlayUtils.muteAudioFocus(mContext, true);
                    mRecorder.setOutputFile(filePath);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    isSuccess = false;
                    this.stopRecord(true);
                    new AlertMsg(mContext,null).setMsg("应用录音权限被禁用，请到设置里更改","确定","").createShow();
                }
            }
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                PlayUtils.muteAudioFocus(mContext, false);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null!=recordListener){
                    recordListener.onRecordSuccess(new File(filePath), recordTime, isSuccess,isCancel);
                }
                mRecorder.release();
                mRecorder = null;
            }
        }
    }

    public interface RecordListener {
        void onRecordSuccess(File file, int voiceLength, boolean isSuccess, boolean isCancel);
        void onTimeAdd(int voiceLength);
    }

}
