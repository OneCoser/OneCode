package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.utils.StringUtils;

public class AlertEdit extends AlertBase {

    public interface OnAlertEditListener {
        boolean initViewStyle(View root,EditText contentEdit,View line, View lineButton,
                           FilletBtView leftButton, FilletBtView rightButton);
        boolean onSubmit(String s);
    }

    private EditText editText;
    private boolean submitLeft;
    private String leftStr, rightStr;
    private OnAlertEditListener editListener;

    public AlertEdit(Context c, OnAlertEditListener listener) {
        super(c);
        this.editListener = listener;
        setCloseClean(true);
    }

    @Override
    public AlertBase create() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createDialog(R.layout.onecode_alert_edit, params);
        return this;
    }

    public AlertEdit setButton(boolean submitLeft, String leftStr, String rightStr) {
        this.submitLeft = submitLeft;
        this.leftStr = leftStr;
        this.rightStr = rightStr;
        return this;
    }

    @Override
    public void createDialogInit(View layout, Dialog d) {
        super.createDialogInit(layout, d);
        editText = (EditText) layout.findViewById(R.id.alert_content);
        View line = layout.findViewById(R.id.alert_line);
        View lineButton = layout.findViewById(R.id.alert_bt_line);
        FilletBtView leftButton = (FilletBtView) layout.findViewById(R.id.alert_bt_left);
        FilletBtView rightButton = (FilletBtView) layout.findViewById(R.id.alert_bt_right);
        if (null != editListener) {
            editListener.initViewStyle(layout,editText,line,lineButton,leftButton,rightButton);
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
                            if (submitLeft) {
                                if (null == editListener || editListener.onSubmit(editText.getText().toString())) {
                                    close();
                                }
                            } else {
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
                            if (submitLeft) {
                                close();
                            } else if (null == editListener || editListener.onSubmit(editText.getText().toString())) {
                                close();
                            }
                        }
                    });
        }
        lineButton.setVisibility(btCount == 2 ? View.VISIBLE : View.GONE);
    }

}