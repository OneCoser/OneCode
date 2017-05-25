package chenhao.lib.onecode.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import org.simple.eventbus.EventBus;
import butterknife.ButterKnife;
import chenhao.lib.onecode.net.HttpClient;
import chenhao.lib.onecode.utils.StringUtils;

public abstract class BaseFragment extends Fragment {

    public float dp;
    public int screenW, screenH;
    public View contentView;

    public abstract void initView();
    public abstract String getPageName();
    protected abstract void systemStatusAction(int status);
    protected abstract View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dp = getResources().getDisplayMetrics().density;
        screenW = getResources().getDisplayMetrics().widthPixels;
        screenH = getResources().getDisplayMetrics().heightPixels;
        EventBus.getDefault().registerSticky(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null!=contentView) {
            ViewGroup group=(ViewGroup) contentView.getParent();
            if (group!=null) {
                group.removeView(contentView);
            }
        }else {
            contentView=getContentView(inflater, container, savedInstanceState);
        }
        if (null!=contentView){
            ButterKnife.bind(this, contentView);
        }
        initView();
        return contentView;
    }

    public final int SYSTEM_STATUS_HIDE = BaseActivity.SYSTEM_STATUS_HIDE;
    public final int SYSTEM_STATUS_LOADING = BaseActivity.SYSTEM_STATUS_LOADING;
    public final int SYSTEM_STATUS_NULL_DATA = BaseActivity.SYSTEM_STATUS_NULL_DATA;
    public final int SYSTEM_STATUS_NET_ERROR = BaseActivity.SYSTEM_STATUS_NET_ERROR;
    public final int SYSTEM_STATUS_API_ERROR = BaseActivity.SYSTEM_STATUS_API_ERROR;
    private LinearLayout system_status_layout;
    public LinearLayout getSystemStatusLayout(){
        if (null==system_status_layout&&null!=getView()&&null!=getView().findViewById(R.id.system_status)){
            system_status_layout=(LinearLayout) getView().findViewById(R.id.system_status);
            system_status_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        return system_status_layout;
    }

    public void showSystemStatus(int status) {
        if (null!=OneCode.getConfig()){
            showSystemStatus(status,
                    status == SYSTEM_STATUS_NET_ERROR?OneCode.getConfig().getSystemStatusApiErrorIcon(getActivity(),getPageName()):
                            status == SYSTEM_STATUS_API_ERROR?OneCode.getConfig().getSystemStatusNetErrorIcon(getActivity(),getPageName()):
                                    OneCode.getConfig().getSystemStatusNullDataIcon(getActivity(),getPageName()),
                    OneCode.getConfig().getSystemStatusLoadingView(getActivity(),getPageName()));
        }else{
            showSystemStatus(status,0,null);
        }
    }

    public void showSystemStatus(int status,int iconResId,View loadingView) {
        if (null != getSystemStatusLayout()) {
            if (getSystemStatusLayout().getChildCount()>0){
                getSystemStatusLayout().removeAllViews();
            }
            if (status==SYSTEM_STATUS_HIDE){
                setVis(getSystemStatusLayout(), View.GONE);
            }else if(status==SYSTEM_STATUS_LOADING){
                if (null!=loadingView){
                    getSystemStatusLayout().addView(loadingView);
                }else{
                    getSystemStatusLayout().addView(View.inflate(getActivity(),R.layout.onecode_layout_loading_small,null));
                }
                setVis(getSystemStatusLayout(), View.VISIBLE);
            }else{
                ImageView system_status_icon=new ImageView(getSystemStatusLayout().getContext());
                system_status_icon.setTag(status);
                if (iconResId==0){
                    iconResId=status == SYSTEM_STATUS_NET_ERROR||status == SYSTEM_STATUS_API_ERROR?
                            R.drawable.onecode_default_icon_internet: R.drawable.onecode_default_icon_nothing;
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
                        systemStatusAction(s);
                    }
                });
                getSystemStatusLayout().addView(system_status_icon);
                setVis(getSystemStatusLayout(), View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        HttpClient.init().cancel(this);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpClient.init().cancel(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public <T extends View> T findV(int id){
        if (null!=contentView&&null!=contentView.findViewById(id)){
            return (T) contentView.findViewById(id);
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