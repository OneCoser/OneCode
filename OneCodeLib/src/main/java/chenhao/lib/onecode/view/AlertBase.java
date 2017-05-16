package chenhao.lib.onecode.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by onecode on 16/6/4.
 */
public abstract class AlertBase{

    public Activity context;
    public boolean cancel = true;
    public boolean canceledTouchOutside = false;
    public DialogInterface.OnDismissListener dismissListener;
    public DialogInterface.OnCancelListener cancelListener;
    public DialogInterface.OnShowListener showListener;
    public DialogInterface.OnKeyListener keyListener;

    public AlertBase(Activity activity) {
        this.context = activity;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.dismissListener = listener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.cancelListener = listener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener listener) {
        this.showListener = listener;
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener listener) {
        this.keyListener = listener;
    }

    public void setCanceledOnTouchOutside(boolean b) {
        this.canceledTouchOutside = b;
    }

    public void setCancelable(boolean b) {
        this.cancel = b;
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

}
