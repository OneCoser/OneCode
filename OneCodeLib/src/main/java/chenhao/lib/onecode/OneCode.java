package chenhao.lib.onecode;

import android.content.Context;
import chenhao.lib.onecode.utils.ImageShow;

public class OneCode {

    private static Context context;
    private static OneCodeConfig codeConfig;
    public static void init(Context c,OneCodeConfig config){
        context=c;
        codeConfig=config;
        ImageShow.init(context);
    }

    public static Context getContext(){
        return context;
    }

    public static OneCodeConfig getConfig(){
        return codeConfig;
    }

    public static boolean isDebug(){
        return null!=codeConfig&&codeConfig.isDebug();
    }

}
