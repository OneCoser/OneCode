package com.chenhao.onecode;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import chenhao.lib.onecode.base.BaseViewHolder;
import chenhao.lib.onecode.base.RefreshBaseActivity;
import chenhao.lib.onecode.image.AlbumListActivity;
import chenhao.lib.onecode.image.GetPhotoInfo;
import chenhao.lib.onecode.utils.LayoutManagerUtil;
import chenhao.lib.onecode.video.VideoListActivity;
import chenhao.lib.onecode.view.TitleView;

/**
 * 所属项目：OneCode
 * 创建日期：2017/5/16
 * 创建人：onecode
 * 修改日期：2017/5/16
 * 修改人：onecode
 * 描述：OneCodeActivity
 */

public class OneCodeActivity extends RefreshBaseActivity<String>{

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return LayoutManagerUtil.getList(this);
    }

    @Bind(R.id.title_view)
    TitleView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleView.setTextIcon("框架代码演示","","",R.drawable.onecode_icon_back_w,0);
        titleView.setShow(TitleView.SHOW_ICON,TitleView.SHOW_NONE);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                onBackPressed();
            }
        });
        loadData(false,false);
    }

    @Override
    public void loadData(boolean getMore, boolean isUser) {
        super.loadData(getMore, isUser);
        getRefreshView().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> data=new ArrayList<String>();
                data.add("选照片");
                data.add("选视频");
                data.add("Item3");
                data.add("Item4");
                data.add("Item5");
                data.add("Item6");
                data.add("Item7");
                data.add("Item8");
                data.add("Item9");
                onDataSuccess(data,SYSTEM_STATUS_NULL_DATA,false);
            }
        },3000);
    }

    @Override
    protected BaseViewHolder<String> getItem(int viewType) {
        return new ItemTest();
    }

    public class ItemTest extends BaseViewHolder<String>{

        @Bind(R.id.test_item)
        TextView item;

        private int index;

        public ItemTest() {
            super(View.inflate(OneCodeActivity.this, R.layout.app_item_test,null));
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (index){
                        case 0:
                            AlbumListActivity.goGetPhoto(OneCodeActivity.this, GetPhotoInfo.getDefualtInfo());
                            break;
                        case 1:
                            VideoListActivity.get(OneCodeActivity.this);
                            break;
                    }
                }
            });
        }

        @Override
        public void initView(String s, int position) {
            this.index=position;
            item.setText(s);
        }
    }

    @Override
    public String getPageName() {
        return "onecode";
    }
}
