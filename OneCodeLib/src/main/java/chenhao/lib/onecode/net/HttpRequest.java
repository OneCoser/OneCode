package chenhao.lib.onecode.net;

import android.os.Build;
import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.base.BaseModule;
import chenhao.lib.onecode.utils.Clog;
import chenhao.lib.onecode.utils.StringUtils;

public class HttpRequest extends Request<JSONObject> {

    private Map<String, String> mHeaders;
    private Map<String, Object> mParams;
    private HttpCallBack callBack;
    private boolean toJsonParams;//是否用json请求

    public HttpRequest(boolean toJsonParams,int method, String url, Map<String, Object> params,Object tag,@NonNull HttpCallBack callBack) {
        this(toJsonParams,method,url,params,null,tag,callBack);
    }

    public HttpRequest(boolean toJsonParams,int method, String url, Map<String, Object> params, Map<String, String> headers,Object tag,@NonNull HttpCallBack callBack) {
        super(method, url, callBack);
        this.toJsonParams=toJsonParams;
        this.callBack = callBack;
        this.mParams = params;
        this.mHeaders=headers;
        if (null!=tag){
            this.setTag(tag);
        }
        setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1));
        if (OneCode.isDebug()){
            callBack.logUrl(method == Method.POST ? "Post:" : "Get:",getUrl());
            if (toJsonParams){
                Clog.h("JsonParams："+(null!=params? JSON.toJSONString(params):"null"));
            }else if(method == Method.POST){
                Clog.h("PostParams："+(null!=params? StringUtils.buildUrl("",params):"null"));
            }
        }
    }

    private String parseJsonParams(){
        String s="";
        if (null!=mParams&&mParams.size()>0){
            s= JSON.toJSONString(mParams);
        }
        return s;
    }

    @Override
    public String getBodyContentType() {
        if (toJsonParams){
            return "application/json; charset="+getParamsEncoding();
        }else{
            return super.getBodyContentType();
        }
    }

    @Override
    public String getPostBodyContentType() {
        return this.getBodyContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (toJsonParams){
            String s=parseJsonParams();
            try {
                return StringUtils.isEmpty(s)?null:s.getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException var2) {
                var2.printStackTrace();
                return null;
            }
        }else{
            return super.getBody();
        }
    }

    @Override
    public byte[] getPostBody() throws AuthFailureError {
        return this.getBody();
    }

    public String getUserAgent() {
        return "Android/" + Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT + " Model/" + Build.MANUFACTURER + "/" + Build.MODEL;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.putAll(super.getHeaders());
        headers.put("User-Agent", getUserAgent());
        headers.put("Charset", getParamsEncoding());
        headers.put("Content-Type", getBodyContentType());
        if (null!=mHeaders&&!mHeaders.isEmpty()){
            headers.putAll(mHeaders);
        }
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams == null || mParams.isEmpty()){
            return super.getParams();
        }else{
            Map<String, String> result = new HashMap<>();
            Iterator<String> keys = mParams.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = mParams.get(key);
                if (value != null) {
                    result.put(key, value.toString());
                }
            }
            return result;
        }
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String charset=HttpHeaderParser.parseCharset(response.headers,getParamsEncoding());
//            String charset=HttpHeaderParser.parseCharset(response.headers);
            String str = new String(response.data,charset);
            Clog.h("parseNetwork-"+charset+":"+str);
            if (StringUtils.isNotEmpty(str)&&str.startsWith("{")){
                return Response.success(BaseModule.parseObject(str),HttpHeaderParser.parseCacheHeaders(response));
            }else{
                return Response.error(new ParseError());
            }
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject json) {
        callBack.httpCode=500;
        boolean isBusinessError=true;
        if (null!=json){
            try{
                callBack.httpMsg=json.getString(callBack.httpMsgName);
                callBack.httpCode=null!=json.get(callBack.httpCodeName)?json.getIntValue(callBack.httpCodeName):500;
                if (callBack.httpCode==callBack.successCode){
                    isBusinessError=false;
                    if (callBack.clazz == Boolean.class){
                        callBack.onSuccess(StringUtils.isNotEmpty(callBack.dataName)?json.getBoolean(callBack.dataName):true);
                    } else if (callBack.clazz == JSONObject.class){
                        callBack.onSuccess(StringUtils.isNotEmpty(callBack.dataName)?json.getJSONObject(callBack.dataName):json);
                    }else if(StringUtils.isNotEmpty(callBack.dataName)){
                        if (json.get(callBack.dataName) instanceof JSONArray){
                            callBack.onSuccess(BaseModule.parseArray(json.getString(callBack.dataName),callBack.clazz));
                        }else if(callBack.clazz==String.class){
                            callBack.onSuccess(json.getString(callBack.dataName));
                        }else{
                            callBack.onSuccess(BaseModule.parseObject(json.getString(callBack.dataName),callBack.clazz));
                        }
                    } else{
                        callBack.onSuccess(BaseModule.parseObject(json.toString(),callBack.clazz));
                    }
                }
            }catch (Exception e){
                callBack.httpMsg="数据解析错误";
                callBack.httpCode=500;
                isBusinessError=true;
                e.printStackTrace();
            }
        }
        if (isBusinessError&&null!=OneCode.getConfig()){
            isBusinessError=!OneCode.getConfig().onErrorBusinessFilter(callBack.httpCode,callBack.httpMsg);
        }
        if (isBusinessError){
            callBack.onErrorBusiness();
            parseCache();
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (null!=error&&null!=error.networkResponse&&OneCode.isDebug()){
            try {
                String str = new String(error.networkResponse.data,HttpHeaderParser.parseCharset(error.networkResponse.headers));
                Clog.h("networkError-"+str);
            } catch (Exception e) {
                super.deliverError(error);
            }
        }
        parseCache();
    }

    private void parseCache(){
        JSONObject json=null;
        try {
            if (null!=getCacheEntry()&&null!=getCacheEntry().data){
                String str=new String(getCacheEntry().data);
                Clog.h("parseCache-"+str);
                if (StringUtils.isNotEmpty(str)&&str.startsWith("{")){
                    json= BaseModule.parseObject(str);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        boolean cacheNull=true;
        if (null!=json){
            try{
                int httpCode=null!=json.get(callBack.httpCodeName)?json.getIntValue(callBack.httpCodeName):500;
                if (httpCode==callBack.successCode){
                    cacheNull=false;
                    if (callBack.clazz == Boolean.class){
                        callBack.onCache(StringUtils.isNotEmpty(callBack.dataName)?json.getBoolean(callBack.dataName):true);
                    } else if (callBack.clazz == JSONObject.class){
                        callBack.onCache(StringUtils.isNotEmpty(callBack.dataName)?json.getJSONObject(callBack.dataName):json);
                    }else if(StringUtils.isNotEmpty(callBack.dataName)){
                        if (json.get(callBack.dataName) instanceof JSONArray){
                            callBack.onCache(BaseModule.parseArray(json.getString(callBack.dataName),callBack.clazz));
                        }else if(callBack.clazz==String.class){
                            callBack.onCache(json.getString(callBack.dataName));
                        }else{
                            callBack.onCache(BaseModule.parseObject(json.getString(callBack.dataName),callBack.clazz));
                        }
                    } else{
                        callBack.onCache(BaseModule.parseObject(json.toString(),callBack.clazz));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (cacheNull){
            callBack.onCache(null);
        }
    }

}
