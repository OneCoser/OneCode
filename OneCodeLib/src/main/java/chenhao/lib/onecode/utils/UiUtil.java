package chenhao.lib.onecode.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import java.util.List;

public class UiUtil {

    private static UiUtil self;

    public static UiUtil init() {
        if (null == self) {
            self = new UiUtil();
        }
        return self;
    }

    private Dialog dialog;

    public void showDialog(Context context, boolean canCelable) {
        cancelDialog();
        try {
            if (null != context) {
                dialog = new Dialog(context, R.style.CustomProgressDialog);
                if (null!=OneCode.getConfig()&&OneCode.getConfig().getDialogResId()!=0){
                    dialog.setContentView(OneCode.getConfig().getDialogResId());
                }else{
                    dialog.setContentView(R.layout.onecode_loading_dialog);
                }
                dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(canCelable);
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {
                        dialog.show();
                    }
                } else {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelDialog() {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (null != dialog) {
                dialog.cancel();
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toast(String msg) {
        toast(OneCode.getContext(),msg);
    }
    public void toast(Context context, String msg) {
        try {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toast(Context context, int resId) {
        toast(context, context.getString(resId));
    }

    public void closeBroads(Activity context) {
        try {
            if (null != context) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                IBinder ib = null != context.getCurrentFocus() ? context.getCurrentFocus().getApplicationWindowToken() : null;
                if (null != ib && imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(ib, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeBroads(Context context, View view) {
        try {
            if (null != context && null != view) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showKeyboard(Activity context, TextView view) {
        try {
            if (null != context && null != view) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends View> T findV(View parent, int id) {
        if (null != parent && null != parent.findViewById(id)) {
            try {
                return (T) parent.findViewById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T extends View> T findV(Activity activity, int id) {
        if (null != activity && null != activity.findViewById(id)) {
            try {
                return (T) activity.findViewById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public PackageInfo getAppInfo(Context context, String packageName) {
        PackageInfo info = null;
        if (null != context && StringUtils.isNotEmpty(packageName)) {
            final PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
            for (int i = 0; i < pinfo.size(); i++) {
                if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                    info = pinfo.get(i);
                    break;
                }
            }
        }
        return info;
    }

    public static final int NET_INVALID = 0;
    public static final int NET_WAP = 1;
    public static final int NET_2G = 2;
    public static final int NET_34G = 3;
    public static final int NET_WIFI = 4;

    private boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    public int netType(Context context) {
        int nt=NET_INVALID;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type== ConnectivityManager.TYPE_WIFI) {
                nt = NET_WIFI;
            } else if (type== ConnectivityManager.TYPE_MOBILE) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                nt = StringUtils.isEmpty(proxyHost)? (isFastMobileNetwork(context) ? NET_34G : NET_2G): NET_WAP;
            }
        }
        return nt;
    }

    public boolean netIsMobile(Context context){
        int type=netType(context);
        return type==NET_WAP||type==NET_2G||type==NET_34G;
    }

    public boolean netIsWifi(Context context){
        return netType(context)==NET_WIFI;
    }

    public boolean netIsInvalid(Context context){
        return netType(context)==NET_INVALID;
    }

    public static void vibrate(Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static void vibrate(Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }


    public void setWindowStatusBarColor(Activity activity, int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
                //底部导航栏
                //window.setNavigationBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
