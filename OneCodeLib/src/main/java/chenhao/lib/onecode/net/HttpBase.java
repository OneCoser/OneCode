package chenhao.lib.onecode.net;

import android.support.annotation.NonNull;
import com.android.volley.Request;
import java.util.LinkedHashMap;
import java.util.Map;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.utils.StringUtils;

public abstract class HttpBase {

    public static <T> Request doHttp(boolean toJsonParams,int method, String url, Map<String, Object> params, Object tag, Class clazz, String dataName, @NonNull HttpCallBack<T> callback) {
        callback.init(clazz,dataName);
        return HttpClient.init().doHttp(toJsonParams,method,url,params,tag,callback);
    }

    public static <T> Request doPost(boolean toJsonParams,String api, Map<String, Object> params, Object tag, Class clazz, String dataName, @NonNull HttpCallBack<T> callback) {
        return doHttp(toJsonParams,Request.Method.POST,
                null!=OneCode.getConfig()?(OneCode.getConfig().getApiBaseUrl() + api):api,
                params, tag, clazz, dataName, callback);
    }
    public static <T> Request doPost(String api, Map<String, Object> params, Object tag, Class clazz, String dataName, @NonNull HttpCallBack<T> callback) {
        return doPost(false,api,params,tag,clazz,dataName,callback);
    }

    public static <T> Request doGet(boolean toJsonParams,String api, Map<String, Object> params, Object tag, Class clazz, String dataName, @NonNull HttpCallBack<T> callback) {
        return doHttp(toJsonParams,Request.Method.GET,
                null!=OneCode.getConfig()?(OneCode.getConfig().getApiBaseUrl() + api):api,
                params, tag, clazz, dataName, callback);
    }
    public static <T> Request doGet(String api, Map<String, Object> params, Object tag, Class clazz, String dataName, @NonNull HttpCallBack<T> callback) {
        return doGet(false,api,params,tag,clazz,dataName,callback);
    }

    public static void cancelAll() {
        HttpClient.init().cancelAll();
    }

    public static void cancel(Object tag) {
        HttpClient.init().cancel(tag);
    }

    public static Map<String, Object> putParams(Object... keyAndParams) {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        if (null!=OneCode.getConfig()){
            OneCode.getConfig().putHttpFixedParams(params);
        }
        if (null != keyAndParams && keyAndParams.length > 1 && keyAndParams.length % 2 == 0) {
            for (int i = 0; i < keyAndParams.length; i += 2) {
                String key = null != keyAndParams[i] ? keyAndParams[i].toString() : "";
                Object value = keyAndParams[i + 1];
                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                    params.put(key, value);
                }
            }
        }
        return params;
    }

}