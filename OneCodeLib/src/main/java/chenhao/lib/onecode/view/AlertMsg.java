package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.utils.StringUtils;

public class AlertMsg extends AlertBase {

    public interface OnAlertMsgListener {
        boolean onClick(boolean isLeft);
    }

    public interface OnAlertMsgViewStyle {
        boolean initStyle(View root, LinearLayout contentLay, TextView msgView, View line, View lineButton,
                          FilletBtView leftButton, FilletBtView rightButton);
    }

    private View contentView;
    private String message, leftStr, rightStr;
    private OnAlertMsgListener msgListener;
    private OnAlertMsgViewStyle msgViewStyle;

    public AlertMsg(Context c, OnAlertMsgListener listener) {
        this(c, listener, null);
    }

    public AlertMsg(Context c, OnAlertMsgListener listener, OnAlertMsgViewStyle viewStyle) {
        super(c);
        setCloseClean(true);
        this.msgListener = listener;
        this.msgViewStyle = viewStyle;
    }

    @Override
    public AlertBase create() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createDialog(R.layout.onecode_alert_msg, params);
        return this;
    }

    public AlertMsg setMsg(View msgView, String leftStr, String rightStr) {
        this.message = "";
        this.contentView = msgView;
        this.leftStr = leftStr;
        this.rightStr = rightStr;
        return this;
    }

    public AlertMsg setMsg(String message, String leftStr, String rightStr) {
        this.contentView = null;
        this.message = message;
        this.leftStr = leftStr;
        this.rightStr = rightStr;
        return this;
    }

    @Override
    public void createDialogInit(View layout, Dialog d) {
        super.createDialogInit(layout, d);
        LinearLayout contentLay = (LinearLayout) layout.findViewById(R.id.alert_content);
        TextView msgView = (TextView) layout.findViewById(R.id.alert_msg);
        View line = layout.findViewById(R.id.alert_line);
        View lineButton = layout.findViewById(R.id.alert_bt_line);
        FilletBtView leftButton = (FilletBtView) layout.findViewById(R.id.alert_bt_left);
        FilletBtView rightButton = (FilletBtView) layout.findViewById(R.id.alert_bt_right);
        if (null != msgViewStyle) {
            msgViewStyle.initStyle(layout, contentLay, msgView, line, lineButton, leftButton, rightButton);
        }
        if (null != contentView) {
            contentLay.removeAllViews();
            contentLay.addView(contentView);
        } else {
            if (StringUtils.isEmpty(message)) {
                message = "未知提示";
            }
            msgView.setText(message);
        }
        int btCount = 0;
        if (StringUtils.isEmpty(leftStr)) {
            leftButton.setVisibility(View.GONE);
        } else {
            btCount += 1;
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setText(leftStr);
            leftButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (null == msgListener || msgListener.onClick(true)) {
                        close();
                    }
                }
            });
        }
        if (StringUtils.isEmpty(rightStr)) {
            rightButton.setVisibility(View.GONE);
        } else {
            btCount += 1;
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setText(rightStr);
            rightButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (null == msgListener || msgListener.onClick(false)) {
                        close();
                    }
                }
            });
        }
        lineButton.setVisibility(btCount == 2 ? View.VISIBLE : View.GONE);
    }

}