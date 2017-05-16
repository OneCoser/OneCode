package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import chenhao.lib.onecode.R;

import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;

public class AlertEdit extends Dialog {

    public AlertEdit(Context context, int theme) {
        super(context, theme);
    }

    public AlertEdit(Context context) {
        super(context);
    }

    public interface OnSubimtListener {
        void onSubmit(String str);
    }

    public static class Builder {

        private boolean canceledTouchOutside = false, cancel = true, submitLeft, canNull;
        private String oldData, hint, leftTitle, rightTitle;
        private int inputType= InputType.TYPE_CLASS_TEXT;
        private Context context;

        private OnSubimtListener onSubimtListener;

        private OnDismissListener dismissListener;

        private OnCancelListener cancelListener;

        private OnShowListener showListener;

        private OnKeyListener keyListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnDismissListener(OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder setOnShowListener(OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean b) {
            this.canceledTouchOutside = b;
            return this;
        }

        public Builder setCancelable(boolean b) {
            this.cancel = b;
            return this;
        }

        public Builder setHint(String h, boolean submitIsLeft, boolean isCanNull) {
            return setHint("",h,submitIsLeft,isCanNull);
        }

        public Builder setHint(String o, String h, boolean submitIsLeft, boolean isCanNull) {
            this.submitLeft = submitIsLeft;
            this.canNull = isCanNull;
            this.oldData = o;
            this.hint = h;
            return this;
        }

        public Builder setInputType(int type){
            this.inputType=type;
            return this;
        }

        public Builder setButton(String left, String right, OnSubimtListener listener) {
            this.onSubimtListener = listener;
            this.rightTitle = right;
            this.leftTitle = left;
            return this;
        }

        public void createShow() {
            this.createA().show();
        }

        private EditText content;

        public AlertEdit createA() {
            final AlertEdit dialog = new AlertEdit(context, R.style.DialogAlert);
            View layout = View.inflate(context, R.layout.onecode_alert_edit, null);
            content = (EditText) layout.findViewById(R.id.alert_content);
            content.setHint(StringUtils.isNotEmpty(hint) ? hint : "请输入内容");
            content.setText(StringUtils.isNotEmpty(oldData) ? oldData : "");
            content.setInputType(inputType);
            int btCount = 0;
            if (StringUtils.isEmpty(leftTitle)) {
                layout.findViewById(R.id.alert_bt_left).setVisibility(View.GONE);
            } else {
                btCount += 1;
                layout.findViewById(R.id.alert_bt_left).setVisibility(View.VISIBLE);
                ((Button) layout.findViewById(R.id.alert_bt_left)).setText(leftTitle);
                layout.findViewById(R.id.alert_bt_left)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (submitLeft && (canNull || StringUtils.isNotEmpty(content.getText()))) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                    if (onSubimtListener != null) {
                                        onSubimtListener.onSubmit(content.getText().toString());
                                    }
                                } else if (!submitLeft) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                } else {
                                    UiUtil.init().toast(context, StringUtils.isNotEmpty(hint) ? hint : "请输入内容");
                                }
                            }
                        });
            }
            //设置确定按钮
            if (StringUtils.isEmpty(rightTitle)) {
                layout.findViewById(R.id.alert_bt_right).setVisibility(View.GONE);
            } else {
                btCount += 1;
                layout.findViewById(R.id.alert_bt_right).setVisibility(View.VISIBLE);
                ((Button) layout.findViewById(R.id.alert_bt_right)).setText(rightTitle);
                layout.findViewById(R.id.alert_bt_right)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (!submitLeft && (canNull || StringUtils.isNotEmpty(content.getText()))) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                    if (onSubimtListener != null) {
                                        onSubimtListener.onSubmit(content.getText().toString());
                                    }
                                } else if (submitLeft) {
                                    dialog.dismiss();
                                    dialog.cancel();
                                } else {
                                    UiUtil.init().toast(context, StringUtils.isNotEmpty(hint) ? hint : "请输入内容");
                                }
                            }
                        });
            }
            if (btCount == 2) {
                layout.findViewById(R.id.alert_bt_line).setVisibility(View.VISIBLE);
            } else {
                layout.findViewById(R.id.alert_bt_line).setVisibility(View.GONE);
            }

            dialog.setCanceledOnTouchOutside(canceledTouchOutside);
            if (null != dismissListener) {
                dialog.setOnDismissListener(dismissListener);
            }
            if (null != cancelListener) {
                dialog.setOnCancelListener(cancelListener);
            }
            if (null != showListener) {
                dialog.setOnShowListener(showListener);
            }
            if (null != keyListener) {
                dialog.setOnKeyListener(keyListener);
            }
            dialog.setCancelable(cancel);

            dialog.setContentView(layout);
            return dialog;
        }
    }

}