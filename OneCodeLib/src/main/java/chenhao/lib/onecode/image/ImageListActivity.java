package chenhao.lib.onecode.image;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import chenhao.lib.onecode.R;
import java.util.ArrayList;
import java.util.List;
import chenhao.lib.onecode.base.BaseViewHolder;
import chenhao.lib.onecode.base.RefreshBaseActivity;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.LayoutManagerUtil;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.view.TitleView;

public class ImageListActivity extends RefreshBaseActivity<Image> {

    @Override
    public int getStatusBarColor() {
        return Color.BLACK;
    }

    private TitleView titleView;

    private GetPhotoInfo info;
    private ArrayList<String> selectImages;

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return LayoutManagerUtil.getGrid(this,4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info=getIntent().getParcelableExtra("info");
        if (null==info){
            info= GetPhotoInfo.getDefualtInfo();
        }
        titleView=findV(R.id.title_view);
        titleView.setBackgroundColor(Color.parseColor("#161616"));
        getRefreshView().setBackgroundColor(Color.parseColor("#333333"));
        getRefreshViewLayout().setBackgroundColor(Color.parseColor("#333333"));
        titleView.setTextIcon("相册","","",R.drawable.onecode_icon_back_w,0);
        titleView.getTitleTextView().setTextColor(Color.WHITE);
        titleView.getRightTextView().setTextColor(Color.WHITE);
        titleView.getRightTextView().setText(String.format("完成%s/%s",info.hasCount,info.maxCount));
        titleView.setShow(TitleView.SHOW_ICON, TitleView.SHOW_TEXT);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action== TitleView.ACTION_LEFT_CLICK){
                    onBackPressed();
                }else if(action== TitleView.ACTION_RIGHT_CLICK){
                    if (null!=selectImages&&selectImages.size()>0){
                        setResult(RESULT_OK, new Intent().putExtra("data",selectImages));
                        finish();
                    }
                }
            }
        });
        selectImages=new ArrayList<>();
        loadData(false,false);
    }

    @Override
    public void loadData(boolean getMore, boolean isUser) {
        super.loadData(getMore, isUser);
        List<Image> dataList=null;
        if (null!=getIntent()){
            dataList=getIntent().getParcelableArrayListExtra("images");
        }
        onDataSuccess(dataList,SYSTEM_STATUS_NULL_DATA,false);
    }

    @Override
    protected BaseViewHolder<Image> getItem(int viewType) {
        return new ImageItemItem();
    }

    class ImageItemItem extends BaseViewHolder<Image> {

        ImageView image;
        ImageView select;

        private Image item;

        public ImageItemItem() {
            super(View.inflate(ImageListActivity.this,R.layout.onecode_item_image_list,null));
            itemView.setLayoutParams(new RecyclerView.LayoutParams(screenW/4,screenW/4));
            image=findV(itemView,R.id.item_image_list_image);
            select=findV(itemView,R.id.item_image_list_select);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null!= item && StringUtils.isNotEmpty(item.path)){
                        SimlpBigImageActivity.show(ImageListActivity.this,item);
                    }
                }
            });
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null!= item &&null!=selectImages){
                        boolean isS=selectImages.size()>0&&selectImages.contains(item.path);
                        if (isS){
                            selectImages.remove(item.path);
                            select.setImageResource(R.drawable.onecode_icon_comm_select_d);
                        }else if((info.hasCount+selectImages.size())<info.maxCount){
                            selectImages.add(item.path);
                            select.setImageResource(R.drawable.onecode_icon_comm_select_s);
                        }
                        titleView.getRightTextView().setText(String.format("完成%s/%s",info.hasCount+selectImages.size(),info.maxCount));
                    }
                }
            });
        }

        @Override
        public void initView(Image i, int position) {
            item =i;
            ImageShow.loadFile(image,item.existsPath());
            boolean isS=null!=selectImages&&selectImages.size()>0&&selectImages.contains(item.path);
            select.setImageResource(isS?R.drawable.onecode_icon_comm_select_s :R.drawable.onecode_icon_comm_select_d);
        }

    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

}
