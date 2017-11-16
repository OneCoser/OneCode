package chenhao.lib.onecode.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
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
    public Unbinder unbinder;

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
        if (null != contentView) {
            ViewGroup group = (ViewGroup) contentView.getParent();
            if (group != null) {
                group.removeView(contentView);
            }
        } else {
            contentView = getContentView(inflater, container, savedInstanceState);
        }
        if (null != contentView) {
            unbinder=ButterKnife.bind(this, contentView);
        }
        initView();
        return contentView;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (null != OneCode.getConfig()) {
            OneCode.getConfig().onFragmentPause(this,getPageName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != OneCode.getConfig()) {
            OneCode.getConfig().onFragmentResume(this,getPageName());
        }
    }

    public final int SYSTEM_STATUS_HIDE = BaseActivity.SYSTEM_STATUS_HIDE;
    public final int SYSTEM_STATUS_LOADING = BaseActivity.SYSTEM_STATUS_LOADING;
    public final int SYSTEM_STATUS_NULL_DATA = BaseActivity.SYSTEM_STATUS_NULL_DATA;
    public final int SYSTEM_STATUS_NET_ERROR = BaseActivity.SYSTEM_STATUS_NET_ERROR;
    public final int SYSTEM_STATUS_API_ERROR = BaseActivity.SYSTEM_STATUS_API_ERROR;
    private LinearLayout system_status_layout;

    public LinearLayout getSystemStatusLayout() {
        if (null == system_status_layout && null != getView() && null != getView().findViewById(R.id.system_status)) {
            system_status_layout = (LinearLayout) getView().findViewById(R.id.system_status);
            system_status_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        return system_status_layout;
    }

    public void showSystemStatus(int status) {
        if (null != getSystemStatusLayout()) {
            if (getSystemStatusLayout().getChildCount()>0){
                getSystemStatusLayout().removeAllViews();
            }
            if (status == SYSTEM_STATUS_HIDE) {
                setVis(getSystemStatusLayout(), View.GONE);
            } else{
                View statusView=null!=OneCode.getConfig()?OneCode.getConfig().getSystemStatusView(getActivity(),getPageName(),status):null;
                if (null==statusView&&status == SYSTEM_STATUS_LOADING){
                    statusView=View.inflate(getActivity(),R.layout.onecode_layout_loading_small,null);
                }else if(null==statusView){
                    ImageView system_status_icon = new ImageView(getSystemStatusLayout().getContext());
                    int iconResId=status == SYSTEM_STATUS_NET_ERROR||status == SYSTEM_STATUS_API_ERROR?
                            R.drawable.onecode_default_icon_internet: R.drawable.onecode_default_icon_nothing;
                    system_status_icon.setImageResource(iconResId);
                    system_status_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    statusView=system_status_icon;
                }
                if (null!=statusView){
                    if (status!=SYSTEM_STATUS_LOADING){
                        statusView.setTag(status);
                        statusView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int s = SYSTEM_STATUS_NULL_DATA;
                                try {
                                    if (StringUtils.isNotEmpty(v.getTag())) {
                                        s = Integer.parseInt(v.getTag().toString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                systemStatusAction(s);
                            }
                        });
                    }
                    getSystemStatusLayout().addView(statusView);
                }
                setVis(getSystemStatusLayout(), View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        HttpClient.init().cancel(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpClient.init().cancel(this);
        if (null!=unbinder){
            unbinder.unbind();
        }
        super.onDestroy();
    }

    public <T extends View> T findV(int id) {
        if (null != contentView && null != contentView.findViewById(id)) {
            return (T) contentView.findViewById(id);
        } else {
            return null;
        }
    }

    public <T extends View> T findV(View root, int id) {
        if (null != root && null != root.findViewById(id)) {
            return (T) root.findViewById(id);
        } else {
            return null;
        }
    }

    public void setVis(View v, int vis) {
        if (null != v && v.getVisibility() != vis) {
            v.setVisibility(vis);
        }
    }

    public void setVis(View v, boolean vis) {
        setVis(v, vis ? View.VISIBLE : View.GONE);
    }

}
