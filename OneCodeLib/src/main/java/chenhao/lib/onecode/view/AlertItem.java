package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import chenhao.lib.onecode.R;

public class AlertItem extends Dialog {
	
    public AlertItem(Context context, int theme) {
        super(context, theme);
    }
 
    public AlertItem(Context context) {
        super(context);
    }
 
    public static class Builder {
 
        private OnDismissListener dismissListener;
        private OnCancelListener cancelListener;
        private OnClickListener clickListener;
        private OnShowListener showListener;
        private OnKeyListener keyListener;
        private boolean canceledTouchOutside=false;
        private boolean cancel=true;
        private Context context;
        private String[] items;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean b){
        	this.cancel=b;
        	return this;
        }

        public Builder setCanceledOnTouchOutside(boolean b){
        	this.canceledTouchOutside=b;
        	return this;
        }

        public Builder setOnKeyListener(OnKeyListener listener){
        	this.keyListener=listener;
        	return this;
        }

        public Builder setOnShowListener(OnShowListener listener){
        	this.showListener=listener;
        	return this;
        }
        public Builder setOnCancelListener(OnCancelListener listener){
        	this.cancelListener=listener;
        	return this;
        }
        public Builder setOnDismissListener(OnDismissListener listener){
        	this.dismissListener=listener;
        	return this;
        }

        public Builder setItems(int itemsID,OnClickListener listener){
        	this.items=context.getResources().getStringArray(itemsID);
        	this.clickListener=listener;
        	return this;
        }

        public Builder setItems(String[] items, OnClickListener listener){
        	this.items=items;
        	this.clickListener=listener;
        	return this;
        }
        
        public void createShow(){
        	this.create().show();
        }
        
        public AlertItem create() {
        	final AlertItem dialog = new AlertItem(context, R.style.DialogAlert);
            float dp=context.getResources().getDisplayMetrics().density;
            FilletLinearLayout layout=new FilletLinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setFillet((int)(3*dp));
            layout.initColor(Color.WHITE, Color.WHITE, Color.WHITE);
        	int itemWidth=(int)(context.getResources().getDisplayMetrics().widthPixels*0.66);
        	if (itemWidth<=0) {
				itemWidth=(int)(190*dp);
			}
        	int itemHeight=(int)(45*dp);
        	LinearLayout.LayoutParams itemParams=new LinearLayout.LayoutParams(itemWidth, itemHeight);
        	LinearLayout.LayoutParams lineParams=new LinearLayout.LayoutParams(itemWidth, (int)(1*dp));
        	for (int i = 0; i < items.length; i++) {
				AlertItemView b=new AlertItemView(context);
                b.setColor(Color.parseColor("#383838"), Color.parseColor("#5fc6ff"));
                b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                b.setBackgroundColor(Color.TRANSPARENT);
                b.setLayoutParams(itemParams);
                b.setGravity(Gravity.CENTER);
            	b.setText(items[i]);
            	b.setTag(i);
            	b.setOnClickListener(new View.OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog.dismiss();
    					if (null!=v.getTag()&&null!=clickListener) {
    						int which= Integer.parseInt(v.getTag().toString());
    						clickListener.onClick(dialog, which);
    					}
    				}
    			});
            	layout.addView(b);
            	if (i<items.length-1) {
					TextView l=new TextView(context);
					l.setLayoutParams(lineParams);
					l.setBackgroundColor(Color.parseColor("#eeeeee"));
					layout.addView(l);
				}
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