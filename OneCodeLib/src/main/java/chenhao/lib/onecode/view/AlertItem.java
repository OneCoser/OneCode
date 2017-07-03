package chenhao.lib.onecode.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlertItem extends AlertBase {

    public interface OnAlertItemListener{
        boolean onClick(int i,String s);
    }

    private float dp;
    private String[] items;
    private OnAlertItemListener itemListener;

    public AlertItem(Context c,OnAlertItemListener listener) {
        super(c);
        setCloseClean(true);
        this.itemListener=listener;
        dp=context.getResources().getDisplayMetrics().density;
    }

    @Override
    public AlertBase create() {
        FilletLinearLayout layout=new FilletLinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setFillet((int)(3*dp));
        layout.initColor(Color.WHITE, Color.WHITE, Color.WHITE);
        createDialog(layout,null);
        return this;
    }

    public AlertItem setItems(int itemsID){
        this.items=context.getResources().getStringArray(itemsID);
        return this;
    }

    public AlertItem setItems(String[] items){
        this.items=items;
        return this;
    }

    @Override
    public void createDialogInit(View v, Dialog d) {
        super.createDialogInit(v, d);
        FilletLinearLayout layout=(FilletLinearLayout)v;
        int itemWidth=(int)(context.getResources().getDisplayMetrics().widthPixels*0.66);
        if (itemWidth<=0) {
            itemWidth=(int)(190*dp);
        }
        int itemHeight=(int)(45*dp);
        LinearLayout.LayoutParams itemParams=new LinearLayout.LayoutParams(itemWidth, itemHeight);
        LinearLayout.LayoutParams lineParams=new LinearLayout.LayoutParams(itemWidth, (int)(1*dp));
        if (null==items||items.length<=0){
            items=new String[]{"请设置数据"};
        }
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
                    int index=(int)v.getTag();
                    if (null==itemListener||itemListener.onClick(index,items[index])){
                        close();
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
    }

}