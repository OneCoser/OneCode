package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by onecode on 16/6/4.
 * 自定义对话框父类
 */
public abstract class AlertBase{

    public Context context;
    public boolean cancel = true,canceledTouchOutside = false,closeClean=false;
    public DialogInterface.OnDismissListener dismissListener;
    public DialogInterface.OnCancelListener cancelListener;
    public DialogInterface.OnShowListener showListener;
    public DialogInterface.OnKeyListener keyListener;

    public AlertBase(Context c) {
        this.context = c;
    }

    public AlertBase setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.dismissListener = listener;
        return this;
    }

    public AlertBase setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.cancelListener = listener;
        return this;
    }

    public AlertBase setOnShowListener(DialogInterface.OnShowListener listener) {
        this.showListener = listener;
        return this;
    }

    public AlertBase setOnKeyListener(DialogInterface.OnKeyListener listener) {
        this.keyListener = listener;
        return this;
    }

    public AlertBase setCanceledOnTouchOutside(boolean b) {
        this.canceledTouchOutside = b;
        return this;
    }

    public AlertBase setCancelable(boolean b) {
        this.cancel = b;
        return this;
    }

    public AlertBase setCloseClean(boolean b){
        this.closeClean=b;
        return this;
    }

    public Dialog dialog;
    public void createDialog(int layoutId,ViewGroup.LayoutParams params) {
        createDialog(View.inflate(context, layoutId, null),params);
    }

    public void createDialog(View layout, ViewGroup.LayoutParams params) {
        dialog = new Dialog(context, getThemeResId());
        if (null!=dismissListener) {
            dialog.setOnDismissListener(dismissListener);
        }
        if (null!=cancelListener) {
            dialog.setOnCancelListener(cancelListener);
        }
        if (null!=showListener) {
            dialog.setOnShowListener(showListener);
        }
        if (null!=keyListener) {
            dialog.setOnKeyListener(keyListener);
        }
        dialog.setCanceledOnTouchOutside(canceledTouchOutside);
        dialog.setCancelable(cancel);
        if (Build.VERSION.SDK_INT >= 19) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        createDialogInit(layout,dialog);
        if (null!=params){
            dialog.setContentView(layout,params);
        }else{
            dialog.setContentView(layout);
        }
    }

    public int getThemeResId(){
        return chenhao.lib.onecode.R.style.DialogAlert;
    }

    public void createDialogInit(View layout, Dialog d){

    }

    public abstract AlertBase create();

    public void createShow() {
        create().show();
    }

    public void show(){
        if (null!=dialog&&!dialog.isShowing()){
            dialog.show();
        }
    }

    public void close(){
        if (null!=dialog){
            dialog.dismiss();
            if (closeClean){
                dialog.cancel();
                dialog=null;
            }
        }
    }

}
