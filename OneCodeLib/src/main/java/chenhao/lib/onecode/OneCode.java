package chenhao.lib.onecode;

import android.content.Context;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.ProjectCheck;
import chenhao.lib.onecode.utils.StringUtils;

public class OneCode {

    private static Context context;
    private static OneCodeConfig codeConfig;
    private static ProjectCheck projectCheck;
    public static void init(Context c,OneCodeConfig config){
        context=c;
        codeConfig=config;
        ImageShow.init(context);
        projectCheck=new ProjectCheck(context.getPackageName());
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

    public static boolean projectOK(){
        return null!=projectCheck&&projectCheck.projectCanUse;
    }
    public static String projectErrorHint(){
        String s=null!=projectCheck?projectCheck.errorHint:"";
        if (StringUtils.isEmpty(s)){
            s="此项目已被禁用!\n请联系项目框架搭建人员!";
        }
        return s;
    }

}
