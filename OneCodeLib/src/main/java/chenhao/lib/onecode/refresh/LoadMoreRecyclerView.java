package chenhao.lib.onecode.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import chenhao.lib.onecode.R;
import chenhao.lib.onecode.view.aviloading.AVLoadingIndicatorView;

public class LoadMoreRecyclerView extends RecyclerView {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = -3;
    private static final int TYPE_HEADER = -4;

    private Adapter mWrapAdapter;
    private View mHeaderView=null;
    private Adapter mAdapter;
    private LoadingListener mLoadingListener;
    private LoadMoreFooterView footLoadMoreView;
    private boolean hasMore = true,isLoadingData = false;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        int loadColor= Color.parseColor("#999999");
        int loadStyle= AVLoadingIndicatorView.BallSpinFadeLoader;
        String noMoreTxt="";
        int noMoreTxtColor= Color.parseColor("#999999");
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.OneCodeRefreshView);
            loadColor=typedArray.getColor(R.styleable.OneCodeRefreshView_avlv_color,loadColor);
            loadStyle=typedArray.getColor(R.styleable.OneCodeRefreshView_avlv_style,loadStyle);
            noMoreTxt=typedArray.getString(R.styleable.OneCodeRefreshView_lmfv_txt_noMore);
            noMoreTxtColor=typedArray.getColor(R.styleable.OneCodeRefreshView_lmfv_txtColor_noMore,noMoreTxtColor);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
        footLoadMoreView = new LoadMoreFooterView(getContext());
        footLoadMoreView.setViewStyle(loadStyle,loadColor,noMoreTxt,noMoreTxtColor);
        footLoadMoreView.setVisibility(GONE);
    }

    public void setHeaderView(View view) {
        setHeaderView(view,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setHeaderView(View view, LoadMoreRecyclerView.LayoutParams params) {
        mHeaderView=view;
        if (null!=mHeaderView&&null!=params){
            mHeaderView.setLayoutParams(params);
        }
    }

    public void loadMoreComplete(boolean hasMore,boolean showNoMore) {
        this.hasMore=hasMore;
        isLoadingData = false;
        if(null!=footLoadMoreView){
            footLoadMoreView.setState(!hasMore&&showNoMore? LoadMoreFooterView.STATE_NOMORE: LoadMoreFooterView.STATE_COMPLETE);
        }
    }

    public void setRefreshState(boolean loading) {
        isLoadingData = loading;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mWrapAdapter = new WrapAdapter();
        super.setAdapter(mWrapAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
        setItemAnimator(null);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount() && hasMore) {
                isLoadingData = true;
                if (null!=footLoadMoreView){
                    footLoadMoreView.setState(LoadMoreFooterView.STATE_LAODING);
                }
                mLoadingListener.onLoadMore();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isLoadingData) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions){
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max){
                max = value;
            }
        }
        return max;
    }

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(getHeadersCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(getHeadersCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(getHeadersCount() + positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(getHeadersCount() + positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(getHeadersCount() + fromPosition, getHeadersCount() + toPosition);
        }
    };

    public int getHeadersCount() {
        return null!=mHeaderView?1:0;
    }

    public int getFootersCount() {
        return null!=footLoadMoreView?1:0;
    }

    private class WrapAdapter extends Adapter<ViewHolder> {

        public WrapAdapter() {
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public boolean isHeader(int position) {
            return position >= 0 && position < getHeadersCount();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - getFootersCount();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new SimpleViewHolder(mHeaderView);
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(footLoadMoreView);
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return TYPE_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            ;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position) {
            if (mAdapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {
        void onLoadMore();
    }
}
