package com.chenhao.onecode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import chenhao.lib.onecode.base.BaseViewHolder;
import chenhao.lib.onecode.base.RefreshBaseActivity;
import chenhao.lib.onecode.image.AlbumListActivity;
import chenhao.lib.onecode.image.GetPhotoInfo;
import chenhao.lib.onecode.utils.LayoutManagerUtil;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;
import chenhao.lib.onecode.video.VideoListActivity;
import chenhao.lib.onecode.view.AlertEdit;
import chenhao.lib.onecode.view.AlertItem;
import chenhao.lib.onecode.view.AlertMsg;
import chenhao.lib.onecode.view.FilletBtView;
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

    @BindView(R.id.title_view)
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
                data.add("提示框");
                data.add("编辑框");
                data.add("选择框");
                data.add("Pages");
                for (int i=1;i<LOAD_COUNT;i++){
                    data.add("Item"+i);
                }
                onDataSuccess(data,SYSTEM_STATUS_NULL_DATA);
            }
        },3000);
    }

    @Override
    protected BaseViewHolder<String> getItem(int viewType) {
        return new ItemTest();
    }

    public class ItemTest extends BaseViewHolder<String>{

        @BindView(R.id.test_item)
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
                        case 2:
                            new AlertMsg(OneCodeActivity.this, new AlertMsg.OnAlertMsgListener() {
                                @Override
                                public boolean onClick(boolean isLeft) {
                                    if (isLeft){
                                        UiUtil.init().toast("是");
                                    }
                                    return true;
                                }
                            }).setMsg("你是不是傻逼？","你说呢？","滚").createShow();
                            break;
                        case 3:
                            new AlertEdit(OneCodeActivity.this, new AlertEdit.OnAlertEditListener() {
                                @Override
                                public boolean initViewStyle(View root, EditText contentEdit, View line, View lineButton, FilletBtView leftButton, FilletBtView rightButton) {
                                    contentEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    contentEdit.setHint("请填写手机号");
                                    contentEdit.setMaxLines(1);
                                    return true;
                                }

                                @Override
                                public boolean onSubmit(String s) {
                                    if (StringUtils.isMobile(s)){
                                        UiUtil.init().toast(s);
                                        return true;
                                    }else{
                                        UiUtil.init().toast("请输入正确手机号");
                                        return false;
                                    }
                                }
                            }).setButton(true,"提交","取消").createShow();
                            break;
                        case 4:
                            new AlertItem(OneCodeActivity.this, new AlertItem.OnAlertItemListener() {
                                @Override
                                public boolean onClick(int index, String s) {
                                    UiUtil.init().toast(s);
                                    return true;
                                }
                            }).setItems(new String[]{"哈哈","呵呵","嘿嘿","啪啪"}).createShow();
                            break;
                        case 5:
                            startActivity(new Intent(OneCodeActivity.this,OneCodePagesActivity.class));
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
