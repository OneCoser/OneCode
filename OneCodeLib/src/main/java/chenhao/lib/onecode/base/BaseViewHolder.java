package chenhao.lib.onecode.base;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.ButterKnife;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder{

    public float dp;
    public int screenW, screenH;
    public Object actionTag;
    public Activity actionActivity;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        dp = itemView.getResources().getDisplayMetrics().density;
        screenW = itemView.getResources().getDisplayMetrics().widthPixels;
        screenH = itemView.getResources().getDisplayMetrics().heightPixels;
    }

    public abstract void initView(T t,int position);

}
