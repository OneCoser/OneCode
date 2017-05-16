package chenhao.lib.onecode.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import chenhao.lib.onecode.R;
import org.simple.eventbus.EventBus;
import butterknife.ButterKnife;
import chenhao.lib.onecode.net.HttpClient;
import chenhao.lib.onecode.utils.ActivityTask;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;

public abstract class BaseActivity extends FragmentActivity {

    public float dp;
    public int screenW, screenH;

    public abstract String getPageName();

    protected abstract void reLoad(int status);

    public int getStatusBarColor(){
        return Color.BLACK;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dp = getResources().getDisplayMetrics().density;
        screenW = getResources().getDisplayMetrics().widthPixels;
        screenH = getResources().getDisplayMetrics().heightPixels;
        super.onCreate(savedInstanceState);
        EventBus.getDefault().registerSticky(this);
        ActivityTask.init().addTask(this);
        try {
            if (null != getActionBar()) {
                getActionBar().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        UiUtil.init().closeBroads(this);
        UiUtil.init().setWindowStatusBarColor(this,getStatusBarColor());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    public final int SYSTEM_STATUS_HIDE = 0;
    public final int SYSTEM_STATUS_LOADING = 1;
    public final int SYSTEM_STATUS_NULL_DATA = 2;
    public final int SYSTEM_STATUS_NET_ERROR = 3;
    public final int SYSTEM_STATUS_API_ERROR = 4;
    private LinearLayout system_status_layout;

    public int getStatusIconNothing(){
        return R.drawable.onecode_default_icon_nothing;
    }
    public int getStatusIconNet(){
        return R.drawable.onecode_default_icon_internet;
    }
    public int getStatusIconApi(){
        return R.drawable.onecode_default_icon_internet;
    }

    public void showSystemStatus(int status) {
        showSystemStatus(status,
                status == SYSTEM_STATUS_NULL_DATA? getStatusIconNothing():
                        status == SYSTEM_STATUS_NET_ERROR?getStatusIconNet():
                                status == SYSTEM_STATUS_API_ERROR?getStatusIconApi():getStatusIconNothing());
    }

    public void showSystemStatus(int status, int iconResId) {
        if (null == system_status_layout && null != findViewById(R.id.system_status)) {
            system_status_layout = (LinearLayout) findViewById(R.id.system_status);
            system_status_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        if (null != system_status_layout) {
            if (system_status_layout.getChildCount()>0){
                system_status_layout.removeAllViews();
            }
            if (status == SYSTEM_STATUS_HIDE) {
                setVis(system_status_layout, View.GONE);
            } else if (status == SYSTEM_STATUS_LOADING) {
                system_status_layout.addView(View.inflate(this,R.layout.onecode_layout_loading_small,null),
                        new LinearLayout.LayoutParams((int)(45*dp),(int)(45*dp)));
                setVis(system_status_layout, View.VISIBLE);
            } else {
                ImageView system_status_icon = new ImageView(system_status_layout.getContext());
                system_status_icon.setTag(status);
                if (iconResId==0){
                    iconResId=getStatusIconNothing();
                }
                system_status_icon.setImageResource(iconResId);
                system_status_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                system_status_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int s = SYSTEM_STATUS_NULL_DATA;
                        try {
                            if (StringUtils.isNotEmpty(v.getTag().toString())) {
                                s = Integer.parseInt(v.getTag().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        reLoad(s);
                    }
                });
                system_status_layout.addView(system_status_icon);
                setVis(system_status_layout, View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UiUtil.init().closeBroads(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ActivityTask.init().checkRemove(this);
        UiUtil.init().closeBroads(this);
        HttpClient.init().cancel(this);
        UiUtil.init().cancelDialog();
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public <T extends View> T findV(int id){
        if (null!=findViewById(id)){
            return (T) findViewById(id);
        }else{
            return null;
        }
    }

    public <T extends View> T findV(View root, int id){
        if (null!=root&&null!=root.findViewById(id)){
            return (T) root.findViewById(id);
        }else{
            return null;
        }
    }

    public void setVis(View v, int vis){
        if (null!=v&&v.getVisibility()!=vis){
            v.setVisibility(vis);
        }
    }

    public void setVis(View v, boolean vis){
        setVis(v,vis? View.VISIBLE: View.GONE);
    }

}
