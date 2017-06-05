package chenhao.lib.onecode.net;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.utils.Clog;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;

public class HttpCallBack<T> implements Response.ErrorListener {

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
        init(Boolean.class, "");
    }

    public void init(Class clazz, String dataName) {
        if (null!= OneCode.getConfig()){
            init(clazz,dataName,
                    OneCode.getConfig().getHttpSuccessCode(url),
                    OneCode.getConfig().getHttpCodeName(url),
                    OneCode.getConfig().getHttpMsgName(url),
                    OneCode.getConfig().getHttpDefaultMsgApiError(),
                    OneCode.getConfig().getHttpDefaultMsgNetError());
        }else{
            init(clazz, dataName, 200, "status","msg","连接服务器出错","网络连接错误,请检查你的网络设置");
        }
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

    public void onErrorBusiness() {
        UiUtil.init().cancelDialog();
        UiUtil.init().toast(StringUtils.isNotEmpty(httpMsg)?httpMsg:httpMsgApiError);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        UiUtil.init().cancelDialog();
        UiUtil.init().toast(httpMsgNetError);
    }

}
