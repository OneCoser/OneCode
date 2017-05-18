package com.chenhao.onecode;

import android.app.Activity;
import android.os.Environment;
import java.io.File;
import java.util.Map;
import chenhao.lib.onecode.OneCodeConfig;

/**
 * 所属项目：OneCode
 * 创建日期：2017/5/16
 * 创建人：onecode
 * 修改日期：2017/5/16
 * 修改人：onecode
 * 描述：OneCodeAppConfig
 */

public class OneCodeAppConfig implements OneCodeConfig{

    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public int getHttpSuccessCode(String url) {
        return 200;
    }

    @Override
    public String getHttpCodeName(String url) {
        return "status";
    }

    @Override
    public String getHttpMsgName(String url) {
        return "msg";
    }

    @Override
    public String getHttpDefaultMsgApiError() {
        return "";
    }

    @Override
    public String getHttpDefaultMsgNetError() {
        return "";
    }

    @Override
    public boolean onErrorBusinessFilter(int code, String msg) {
        return false;
    }

    @Override
    public String getApiBaseUrl() {
        return "";
    }

    @Override
    public void putHttpFixedParams(Map<String, Object> params) {

    }

    @Override
    public int getDialogResId() {
        return 0;
    }

    @Override
    public String getCachePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "OneCodeApp" + File.separator;
            try {
                File file = new File(path);
                if (null != file && !file.exists()) {
                    file.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return path;
        } else {
            return OneCodeApp.getInstance().getCacheDir().getAbsolutePath() + File.separator;
        }
    }

    @Override
    public int getDefaultStatusBarColor(Activity a) {
        return 0;
    }

    @Override
    public int getDefaultImageLoadResId() {
        return 0;
    }

    @Override
    public int getDefaultHeadLoadResId() {
        return 0;
    }

}
