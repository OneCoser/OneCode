package chenhao.lib.onecode.net;

import android.support.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import java.io.File;
import java.util.Map;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.utils.StringUtils;

public class HttpClient {

    private static HttpClient http;

    public static HttpClient init() {
        if (null == http) {
            http = new HttpClient();
        }
        return http;
    }

    private RequestQueue requestQueue;

    public synchronized RequestQueue getRequestQueue() {
        if (requestQueue != null) {
            return requestQueue;
        }
        requestQueue = new RequestQueue(new DiskBasedCache(new File(OneCode.getContext().getCacheDir(), "volley"), 100 * 1024 * 1024), new BasicNetwork(new HurlStack()), 10);
        requestQueue.start();
        return requestQueue;
    }

    public <T> Request doHttp(boolean toJsonParams, int method, String url, Map<String, Object> params, Object tag, @NonNull HttpCallBack<T> callback) {
        Request req=null!=OneCode.getConfig()?OneCode.getConfig().customDoHttp(toJsonParams,method,url,params,tag,callback):null;
        if (null==req){
            req = new HttpRequest(toJsonParams,method,
                    method == Request.Method.POST||toJsonParams?url: StringUtils.buildUrl(url, params),
                    method == Request.Method.POST||toJsonParams?params:null,
                    tag,callback);
        }
        return getRequestQueue().add(req);
    }

    public void cancelAll() {
        if (requestQueue != null) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    public void cancel(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
