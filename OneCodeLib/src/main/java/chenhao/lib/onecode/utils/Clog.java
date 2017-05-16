package chenhao.lib.onecode.utils;

import android.util.Log;
import chenhao.lib.onecode.OneCode;

public class Clog {

    public static final String TAG="onecode-";
    private static final boolean CAN_LOG= OneCode.isDebug();

    public static void a(String msg){
        if (CAN_LOG){
            if (StringUtils.isEmpty(msg)){
                msg="null";
            }
            Log.i(TAG+"avLog",msg);
        }
    }

    public static void c(String msg){
        if (CAN_LOG){
            if (StringUtils.isEmpty(msg)){
                msg="null";
            }
            Log.i(TAG+"chatLog",msg);
        }
    }

    public static void i(String msg){
        if (CAN_LOG){
            if (StringUtils.isEmpty(msg)){
                msg="null";
            }
            Log.i(TAG+"infoLog",msg);
        }
    }

    public static void h(String msg){
        if (CAN_LOG){
            if (StringUtils.isEmpty(msg)){
                msg="null";
            }
            Log.i(TAG+"httpLog",msg);
        }
    }

    public static void p(String msg){
        if (CAN_LOG){
            if (StringUtils.isEmpty(msg)){
                msg="null";
            }
            Log.i(TAG+"playLog",msg);
        }
    }

}
