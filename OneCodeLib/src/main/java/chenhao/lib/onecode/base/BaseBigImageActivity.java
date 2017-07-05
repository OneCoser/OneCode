package chenhao.lib.onecode.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.image.Image;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.view.SimlpViewPager;
import chenhao.lib.onecode.view.TitleView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public abstract class BaseBigImageActivity extends BaseActivity {

    @Override
    public int getStatusBarColor() {
        return Color.BLACK;
    }

    private TitleView titleView;
    private SimlpViewPager imagePager;

    private List<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onecode_activity_base_big_image);
        titleView=findV(R.id.title_view);
        imagePager=findV(R.id.big_image_pager);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action== TitleView.ACTION_LEFT_CLICK){
                    onBackPressed();
                }
            }
        });
    }

    public void showImages(List<Image> list, int showIndex){
        images=list;
        if (null==images){
            images=new ArrayList<>();
        }
        if (images.size()>0){
            imagePager.setAdapter(new BigImageAdapter());
            titleView.setTitle(String.format("%s/%s",1,images.size()));
            imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    titleView.setTitle(String.format("%s/%s",position+1,images.size()));
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            if (showIndex>0&&showIndex<images.size()){
                imagePager.setCurrentItem(showIndex);
            }
        }else{
            finish();
        }
    }

    private class BigImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (null!=container&&object instanceof View){
                container.removeView((View)object);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return initImageView(container,position,images.get(position).checkUrl());
        }

    }

    public View initImageView(ViewGroup container,int position,String url){
        PhotoView photoView=new PhotoView(BaseBigImageActivity.this);
        photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setOnLongClickListener(onLongClickListener);
        photoView.setOnViewTapListener(onViewTapListener);
        photoView.setTag(url);
        container.addView(photoView);
        ImageShow.loadImage(photoView,url);
        return photoView;
    }

//    private View getShowView(String url){
//        if (StringUtils.startWithHttp(url)|| StringUtils.startWithTag(url,"res:")){
//            ScrollView view=new ScrollView(BaseBigImageActivity.this);
//            view.setVerticalScrollBarEnabled(false);
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            LinearLayout layout=new LinearLayout(BaseBigImageActivity.this);
//            view.addView(layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            layout.setOrientation(LinearLayout.VERTICAL);
//            layout.setMinimumHeight(screenH);
//            layout.setGravity(Gravity.CENTER);
//            LargeDraweeView draweeView=new LargeDraweeView(BaseBigImageActivity.this);
//            draweeView.setAutoHeight(true);
//            layout.addView(draweeView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenH));
//            ImageShow.setHierarchyDefault(draweeView);
//            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
//            draweeView.setOnLongClickListener(onLongClickListener);
//            draweeView.setOnClickListener(onClickListener);
//            draweeView.setTag(url);
//            ImageShow.loadLarge(draweeView,url,true);
////            container.addView(view);
//            return view;
//        }else{
//            if (StringUtils.startWithTag(url,"file://")){
//                url=url.replace("file://","");
//            }
//            PhotoView photoView=new PhotoView(BaseBigImageActivity.this);
//            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            photoView.setOnViewTapListener(onViewTapListener);
//            photoView.setTag(url);
////            container.addView(photoView);
//            ImageShow.loadImage(photoView,url);
//            return photoView;
//        }
//    }
//    private View.OnClickListener onClickListener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            setVis(titleView,titleView.getVisibility()== View.GONE? View.VISIBLE: View.GONE);
//        }
//    };

    private PhotoViewAttacher.OnViewTapListener onViewTapListener=new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float x, float y) {
            setVis(titleView,titleView.getVisibility()== View.GONE? View.VISIBLE: View.GONE);
        }
    };

    private View.OnLongClickListener onLongClickListener=new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return onLongClickImage(null!=v.getTag()?v.getTag().toString():"");
        }
    };

    public abstract boolean onLongClickImage(String image);

}
