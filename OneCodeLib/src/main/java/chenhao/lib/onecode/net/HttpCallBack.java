package chenhao.lib.onecode.net;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import chenhao.lib.onecode.utils.Clog;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;

public class HttpCallBack<T> implements Response.ErrorListener {

    private final static int DEFAULT_SUCCESS_CODE = 200;
    private final static String DEFAULT_HTTP_MSG_NAME = "msg";
    private final static String DEFAULT_HTTP_CODE_NAME = "status";
    private final static String DEFAULT_HTTP_MSG_API = "连接服务器出错";
    private final static String DEFAULT_HTTP_MSG_NET = "网络连接错误,请检查你的网络设置";

    public int httpCode;
    public String httpMsg;

    public Class clazz;
    public int successCode;
    public String dataName, httpCodeName, httpMsgName,httpMsgApiError,httpMsgNetError;

    private String url;
    public void logUrl(String logTag,String str){
        url=str;
        Clog.h(logTag+url);
    }

    public String getLogUrl(){
        return url;
    }

    public HttpCallBack() {
        init(Boolean.class, "", DEFAULT_SUCCESS_CODE, DEFAULT_HTTP_CODE_NAME, DEFAULT_HTTP_MSG_NAME);
    }

    public HttpCallBack(Class clazz) {
        init(clazz, "", DEFAULT_SUCCESS_CODE, DEFAULT_HTTP_CODE_NAME, DEFAULT_HTTP_MSG_NAME);
    }

    public HttpCallBack(Class clazz, String dataName) {
        init(clazz, dataName, DEFAULT_SUCCESS_CODE, DEFAULT_HTTP_CODE_NAME, DEFAULT_HTTP_MSG_NAME);
    }

    public HttpCallBack(Class clazz, String dataName, int successCode, String httpCodeName, String httpMsgName) {
        init(clazz, dataName, successCode, httpCodeName, httpMsgName);
    }

    public void init(Class clazz, String dataName) {
        init(clazz, dataName, DEFAULT_SUCCESS_CODE, DEFAULT_HTTP_CODE_NAME, DEFAULT_HTTP_MSG_NAME);
    }

    public void init(Class clazz, String dataName, int successCode, String httpCodeName, String httpMsgName) {
        init(clazz, dataName,successCode,httpCodeName,httpMsgName,DEFAULT_HTTP_MSG_API,DEFAULT_HTTP_MSG_NET);
    }

    public void init(Class clazz, String dataName, int successCode, String httpCodeName, String httpMsgName,String httpMsgApiError,String httpMsgNetError) {
        this.clazz = clazz;
        this.dataName = dataName;
        this.successCode = successCode;
        this.httpCodeName = httpCodeName;
        this.httpMsgName = httpMsgName;
        this.httpMsgApiError=httpMsgApiError;
        this.httpMsgNetError=httpMsgNetError;
    }

    public void onSuccess(T t) {
        UiUtil.init().cancelDialog();
    }

    public void onCache(T t) {
        UiUtil.init().cancelDialog();
    }

    public boolean onErrorBusinessFilter(){
        return true;
    }

    public void onErrorBusiness() {
        UiUtil.init().cancelDialog();
        UiUtil.init().toast(StringUtils.isNotEmpty(httpMsg)?httpMsg :StringUtils.isNotEmpty(httpMsgApiError)?httpMsgApiError:DEFAULT_HTTP_MSG_API);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        UiUtil.init().cancelDialog();
        UiUtil.init().toast(StringUtils.isNotEmpty(httpMsgNetError)?httpMsgNetError:DEFAULT_HTTP_MSG_NET);
    }

}
