package chenhao.lib.onecode;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.volley.Request;

import java.util.Map;

import chenhao.lib.onecode.net.HttpCallBack;
import chenhao.lib.onecode.video.Video;

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

    //网络请求业务错误时过滤特定错误码 返回true代表已经在方法里过滤了错误 就不会响应callback的onErrorBusiness方法了
    boolean onErrorBusinessFilter(int code,String msg);

    //网络请求地址前缀
    String getApiBaseUrl();

    //网络请求固定参数
    void putHttpFixedParams(Map<String, Object> params);

    //加载菊花的布局文件id
    int getDialogResId();

    //页面不同状态需要显示的View
    View getSystemStatusView(Context context,String pageName,int status);

    //缓存路径
    String getCachePath();

    //statusbar默认颜色
    int getDefaultStatusBarColor(Activity a);

    //加载图片时的默认图
    int getDefaultImageLoadResId();

    //加载头像时的默认图
    int getDefaultHeadLoadResId();

    //选择视频时是否用第三方播放视频 true则代表已处理播放视频 不会再跳系统播放了
    boolean goVideoPlay(Video video);

    //自定义网络请求&解析
    <T> Request customDoHttp(boolean toJsonParams, int method, String url, Map<String, Object> params, Object tag, @NonNull HttpCallBack<T> callback);

    void onActivityResume(Activity a,String pageName);

    void onActivityPause(Activity a,String pageName);

    void onFragmentResume(Fragment f,String pageName);

    void onFragmentPause(Fragment f,String pageName);

}
