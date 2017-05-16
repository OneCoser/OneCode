package chenhao.lib.onecode.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import chenhao.lib.onecode.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import butterknife.Bind;
import chenhao.lib.onecode.refresh.LoadMoreRecyclerView;
import chenhao.lib.onecode.refresh.PullToRefreshView;
import chenhao.lib.onecode.utils.UiUtil;

public abstract class RefreshBaseActivity<T> extends BaseActivity {

    public int nowPage,LOAD_COUNT;
    public RefreshBaseAdapter adapter;
    public List<T> list = new ArrayList<>();
    public boolean hasMore = true, isclearList = false, isLoading = false;

    private LoadMoreRecyclerView refreshView;
    private PullToRefreshView refreshViewLayout;

    public int getLayoutId() {
        return R.layout.onecode_activity_refresh_default;
    }

    public abstract RecyclerView.LayoutManager getLayoutManager();

    public LoadMoreRecyclerView getRefreshView(){
        return refreshView;
    }

    public PullToRefreshView getRefreshViewLayout(){
        return refreshViewLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            refreshView=findV(R.id.refresh_view);
            refreshViewLayout=findV(R.id.refresh_view_layout);
            refreshViewLayout.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    hasMore=true;
                    loadData(false,true);
                }
                @Override
                public void onRefreshStauts(int stauts) {}
            });
            refreshView.setLayoutManager(getLayoutManager());
            refreshView.setLoadingListener(new LoadMoreRecyclerView.LoadingListener() {
                @Override
                public void onLoadMore() {
                    loadData(true,true);
                }
            });
            adapter = new RefreshBaseAdapter();
            refreshView.setAdapter(adapter);
            LOAD_COUNT= 20;
        } else {
            this.finish();
        }
    }

    public void setRefreshing(boolean b){
        refreshView.setRefreshState(b);
        refreshViewLayout.setRefreshing(b);
    }

    public void loadData(boolean getMore,boolean isUser) {
        if (isLoading) {
            showSystemStatus(SYSTEM_STATUS_LOADING);
            return;
        }
        refreshView.setRefreshState(true);
        isclearList = !getMore;
        isLoading = true;
        if (getMore) {
            nowPage = list.size() / LOAD_COUNT;
            if (list.size() % LOAD_COUNT != 0) {
                nowPage += 1;
            }
            nowPage += 1;
        } else {
            nowPage = 1;
            if (!isUser){
                showSystemStatus(SYSTEM_STATUS_LOADING);
            }
        }
    }

    public boolean canShowNoMore(){
        return null!=list&&list.size()>=LOAD_COUNT;
    }

    public void onDataSuccess(List<T> newData, int status) {
        onDataSuccess(newData,status,null != newData && newData.size() >= LOAD_COUNT);
    }

    public void onDataSuccess(List<T> newData, int status, boolean more) {
        onDataSuccess(newData,status,more,null);
    }

    public void onDataSuccess(List<T> newData, int status, Comparator<T> comparator) {
        onDataSuccess(newData,status,null != newData && newData.size() >= LOAD_COUNT,comparator);
    }

    public void onDataSuccess(List<T> newData, int status, boolean more, Comparator<T> comparator) {
        isLoading = false;
        if (null == list) {
            list = new ArrayList<>();
        }
        this.hasMore=more;
        if (isclearList) {
            list.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        setRefreshing(false);
        if (null != newData && newData.size() > 0) {
            int positionStart = list.size();
            list.addAll(newData);
            if (list.size()>1&&null!=comparator){
                Collections.sort(list,comparator);
            }
            if (adapter != null) {
                adapter.notifyItemRangeInserted(positionStart, newData.size());
            } else {
                adapter = new RefreshBaseAdapter();
                refreshView.setAdapter(adapter);
            }
        }
        refreshView.loadMoreComplete(hasMore,canShowNoMore());
        UiUtil.init().cancelDialog();
        showSystemStatus(list.size()<=0?status:SYSTEM_STATUS_HIDE);
    }

    public void refreshAdapter(int start,int count){
        if (null!=adapter){
            if (start>=0&&count>0){
                adapter.notifyItemRangeChanged(start,count);
            }else if(start>=0){
                adapter.notifyItemChanged(start);
            }else{
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void killItem(int dataIndex){
        if (dataIndex>=0&&null!=list&&dataIndex<list.size()){
            list.remove(dataIndex);
            refreshAdapter(-1,0);
        }
    }

    public class RefreshBaseAdapter extends RecyclerView.Adapter<BaseViewHolder<T>>{
        @Override
        public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
            return getItem(viewType);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
            initHolder(holder,position);
        }

        @Override
        public int getItemViewType(int position) {
            return getItemType(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    protected int getItemType(T t) {
        return 0;
    }

    protected void initHolder(BaseViewHolder<T> holder, int position) {
        holder.initView(list.get(position),position);
    }

    protected abstract BaseViewHolder<T> getItem(int viewType);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=refreshView) {
            refreshView.destroyDrawingCache();
        }
    }

    @Override
    protected void reLoad(int status) {
        loadData(false,false);
    }
}
