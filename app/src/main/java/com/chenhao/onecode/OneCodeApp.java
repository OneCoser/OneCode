package com.chenhao.onecode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import chenhao.lib.onecode.OneCode;

/**
 * 所属项目：OneCode
 * 创建日期：2017/5/16
 * 创建人：onecode
 * 修改日期：2017/5/16
 * 修改人：onecode
 * 描述：OneCodeApp
 */

public class OneCodeApp extends MultiDexApplication {

    private static OneCodeApp INSTANCE;

    public static OneCodeApp getInstance() {
        return INSTANCE == null ? INSTANCE = new OneCodeApp() : INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        if (getPackageName().equals(getCurProcessName(getApplicationContext()))) {
            OneCode.init(getApplicationContext(),new OneCodeAppConfig());
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    StrictMode.VmPolicy.Builder sBuilder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(sBuilder.build());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

}