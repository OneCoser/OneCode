package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.utils.StringUtils;

public class AlertMsg extends AlertBase {

    public interface OnAlertMsgListener{
        boolean onClick(boolean isLeft);
    }

    private View contentView;
    private String message,leftStr,rightStr;
    private OnAlertMsgListener msgListener;

    public AlertMsg(Context c, OnAlertMsgListener listener) {
        super(c);
        setCloseClean(true);
        this.msgListener=listener;
    }

    @Override
    public AlertBase create() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createDialog(R.layout.onecode_alert_msg, params);
        return this;
    }

    public AlertMsg setMsg(View msgView, String leftStr, String rightStr){
        this.message="";
        this.contentView=msgView;
        this.leftStr=leftStr;
        this.rightStr=rightStr;
        return this;
    }

    public AlertMsg setMsg(String message, String leftStr, String rightStr){
        this.contentView=null;
        this.message=message;
        this.leftStr=leftStr;
        this.rightStr=rightStr;
        return this;
    }

    @Override
    public void createDialogInit(View layout, Dialog d) {
        super.createDialogInit(layout, d);
        if (null!=contentView){
            ((LinearLayout)layout.findViewById(R.id.alert_content)).removeAllViews();
            ((LinearLayout)layout.findViewById(R.id.alert_content)).addView(contentView);
        }else{
            if (StringUtils.isEmpty(message)) {
                message="未知提示";
            }
            ((TextView) layout.findViewById(R.id.alert_msg)).setText(message);
        }
        int btCount=0;
        if (StringUtils.isEmpty(leftStr)) {
            layout.findViewById(R.id.alert_bt_left).setVisibility(View.GONE);
        }else {
            btCount+=1;
            layout.findViewById(R.id.alert_bt_left).setVisibility(View.VISIBLE);
            ((Button) layout.findViewById(R.id.alert_bt_left)).setText(leftStr);
            layout.findViewById(R.id.alert_bt_left)
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (null==msgListener||msgListener.onClick(true)){
                                close();
                            }
                        }
                    });
        }
        //设置确定按钮
        if (StringUtils.isEmpty(rightStr)) {
            layout.findViewById(R.id.alert_bt_right).setVisibility(View.GONE);
        }else {
            btCount+=1;
            layout.findViewById(R.id.alert_bt_right).setVisibility(View.VISIBLE);
            ((Button) layout.findViewById(R.id.alert_bt_right)).setText(rightStr);
            layout.findViewById(R.id.alert_bt_right)
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (null==msgListener||msgListener.onClick(false)){
                                close();
                            }
                        }
                    });
        }
        if (btCount==2) {
            layout.findViewById(R.id.alert_bt_line).setVisibility(View.VISIBLE);
        } else {
            layout.findViewById(R.id.alert_bt_line).setVisibility(View.GONE);
        }
    }

}