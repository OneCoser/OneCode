package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import chenhao.lib.onecode.R;

import chenhao.lib.onecode.utils.StringUtils;

public class Alert extends Dialog {

    public Alert(Context context, int theme) {
        super(context, theme);
    }

    public Alert(Context context) {
        super(context);
    }

    public static class Builder {

    	private boolean canceledTouchOutside=false;
    	private String message,leftTitle,rightTitle;
        private View contentView;
        private Context context;
        private boolean cancel=true;

        private OnClickListener leftClickListener,rightClickListener;

        private OnDismissListener dismissListener;

        private OnCancelListener cancelListener;

        private OnShowListener showListener;

        private OnKeyListener keyListener;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder(Context context, View contentV) {
            this.context = context;
            this.contentView=contentV;
        }


        public Builder setOnDismissListener(OnDismissListener listener){
        	this.dismissListener=listener;
        	return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener){
        	this.cancelListener=listener;
        	return this;
        }

        public Builder setOnShowListener(OnShowListener listener){
        	this.showListener=listener;
        	return this;
        }

        public Builder setOnKeyListener(OnKeyListener listener){
        	this.keyListener=listener;
        	return this;
        }

        public Builder setCanceledOnTouchOutside(boolean b){
        	this.canceledTouchOutside=b;
        	return this;
        }

        public Builder setCancelable(boolean b){
        	this.cancel=b;
        	return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setLeftButton(String text,
                OnClickListener listener) {
            this.leftTitle = text;
            this.leftClickListener = listener;
            return this;
        }

        public Builder setRightButton(String text,
                OnClickListener listener) {
            this.rightTitle = text;
            this.rightClickListener = listener;
            return this;
        }

        public Builder setLeftButton(int textId,
                OnClickListener listener) {
            return setLeftButton(context.getString(textId),listener);
        }

        public Builder setRightButton(int textId,
                OnClickListener listener) {
            return setRightButton(context.getString(textId),listener);
        }
        
        public void createShow(){
        	this.createA().show();
        }
        
        public Alert createA() {
        	final Alert dialog = new Alert(context, R.style.DialogAlert);
            View layout = View.inflate(context, R.layout.onecode_alert, null);
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
            if (StringUtils.isEmpty(leftTitle)) {
            	layout.findViewById(R.id.alert_bt_left).setVisibility(View.GONE);
            }else {
            	btCount+=1;
            	layout.findViewById(R.id.alert_bt_left).setVisibility(View.VISIBLE);
            	((Button) layout.findViewById(R.id.alert_bt_left)).setText(leftTitle);
            	layout.findViewById(R.id.alert_bt_left)
            	.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View v) {
            			dialog.dismiss();
                        dialog.cancel();
            			if (null!=leftClickListener) {
            				leftClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
            			}
            		}
            	});
			}
            //设置确定按钮
            if (StringUtils.isEmpty(rightTitle)) {
				layout.findViewById(R.id.alert_bt_right).setVisibility(View.GONE);
			}else {
				btCount+=1;
            	layout.findViewById(R.id.alert_bt_right).setVisibility(View.VISIBLE);
				((Button) layout.findViewById(R.id.alert_bt_right)).setText(rightTitle);
				layout.findViewById(R.id.alert_bt_right)
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
                        dialog.cancel();
						if (rightClickListener != null) {
							rightClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					}
				});
			}
            if (btCount==2) {
            	layout.findViewById(R.id.alert_bt_line).setVisibility(View.VISIBLE);
			} else {
				layout.findViewById(R.id.alert_bt_line).setVisibility(View.GONE);
			}

            dialog.setCanceledOnTouchOutside(canceledTouchOutside);
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
            dialog.setCancelable(cancel);
            
            dialog.setContentView(layout);
            return dialog;
        }
    }
 
}