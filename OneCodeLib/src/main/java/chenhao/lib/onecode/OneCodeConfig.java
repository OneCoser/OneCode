package chenhao.lib.onecode;

import java.util.Map;

public interface OneCodeConfig {

    //是否是调试模式
    boolean isDebug();

    //网络请求成功状态码
    int getHttpSuccessCode(String url);

    //网络请求状态码字段名
    String getHttpCodeName(String url);

    //网络请求接口信息字段名
    String getHttpMsgName(String url);

    //网络请求业务错误时默认提示语
    String getHttpDefaultMsgApiError();

    //网络请求网络错误时默认提示语
    String getHttpDefaultMsgNetError();

    //网络请求地址前缀
    String getApiBaseUrl();

    //网络请求固定参数
    void putHttpFixedParams(Map<String, Object> params);

    //加载菊花的布局文件id
    int getDialogResId();

    //缓存路径
    String getCachePath();

}
