package chenhao.lib.onecode.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import chenhao.lib.onecode.base.BaseViewHolder;
import chenhao.lib.onecode.base.RefreshBaseActivity;
import chenhao.lib.onecode.image.crop.ClipActivity;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.LayoutManagerUtil;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;
import chenhao.lib.onecode.view.TitleView;

public class AlbumListActivity extends RefreshBaseActivity<ImageBucket> {

    public static final int RECODE_GET_PHOTO=16;
    public static void goGetPhoto(Activity a, GetPhotoInfo info){
        Intent intent=new Intent(a, AlbumListActivity.class);
        intent.putExtra("info",info);
        a.startActivityForResult(intent,RECODE_GET_PHOTO);
    }

    public static final int RECODE_GET_PHOTO_CAMERA=17;
    public static void goCamera(Activity a, String imageName){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(imageName);
        imageFile.getParentFile().mkdirs();
        cameraIntent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
                .putExtra("return-data", true)
                .putExtra("autofocus", true);
        a.startActivityForResult(cameraIntent, RECODE_GET_PHOTO_CAMERA);
    }
    public static final int RECODE_GET_PHOTO_CROP=18;
    public static void goCrop(Activity a, String path, int w, int h, boolean isFixed){
        Intent intent=new Intent(a, ClipActivity.class);
        intent.putExtra("isFixed",isFixed);
        intent.putExtra("path",path);
        intent.putExtra("height",h);
        intent.putExtra("width",w);
        a.startActivityForResult(intent, RECODE_GET_PHOTO_CROP);
    }

    @Override
    public int getStatusBarColor() {
        return Color.BLACK;
    }

    private TitleView titleView;

    private GetPhotoInfo info;
    private AlbumHelper helper;
    private String imageFilePath;

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return LayoutManagerUtil.getList(this);
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
        titleView.setTextIcon("相册","","拍照", R.drawable.onecode_icon_back_w,0);
        titleView.getTitleTextView().setTextColor(Color.WHITE);
        titleView.getRightTextView().setTextColor(Color.WHITE);
        titleView.setShow(TitleView.SHOW_ICON,info.canCamera? TitleView.SHOW_TEXT: TitleView.SHOW_NONE);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action== TitleView.ACTION_LEFT_CLICK){
                    onBackPressed();
                }else if(action== TitleView.ACTION_RIGHT_CLICK){
                    startGoCamera();
                }
            }
        });
        helper = AlbumHelper.getNewHelper();
        helper.init(getApplicationContext());
        loadData(false,false);
        if (info.onlyCamera){
            startGoCamera();
        }
    }

    private void startGoCamera(){
        if (null!= OneCode.getConfig()){
            imageFilePath = OneCode.getConfig().getCachePath() + System.currentTimeMillis() + ".jpg";
            goCamera(AlbumListActivity.this, imageFilePath);
        }
    }

    @Override
    public void loadData(boolean getMore, boolean isUser) {
        super.loadData(getMore, isUser);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ImageBucket> dataList=new ArrayList<ImageBucket>();
                try {
                    dataList.addAll(helper.getImagesBucketList(isclearList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(dataList,"album_list_success");
            }
        }).start();
    }

    @Subscriber(tag = "album_list_success")
    public void eventBusList(List<ImageBucket> dataList){
        onDataSuccess(dataList,SYSTEM_STATUS_NULL_DATA,false);
    }

    @Override
    protected BaseViewHolder<ImageBucket> getItem(int viewType) {
        return new ImageBucketItem();
    }

    class ImageBucketItem extends BaseViewHolder<ImageBucket> {

        ImageView image;
        TextView name;

        private ImageBucket bucket;

        public ImageBucketItem() {
            super(View.inflate(AlbumListActivity.this,R.layout.onecode_item_image_bucket,null));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null!=bucket&&null!=bucket.imageList&&bucket.imageList.size()>0){
                        Intent intent=new Intent(AlbumListActivity.this,ImageListActivity.class);
                        intent.putParcelableArrayListExtra("images",bucket.imageList);
                        intent.putExtra("info", info);
                        startActivityForResult(intent, 11);
                    }
                }
            });
            image=findV(itemView,R.id.item_image_bucket_image);
            name=findV(itemView,R.id.item_image_bucket_name);
        }

        @Override
        public void initView(ImageBucket ib, int position) {
            bucket=ib;
            name.setText(String.format("%s(%s)",ib.bucketName,ib.count));
            Image item=null!=ib.imageList&&ib.imageList.size()>0?ib.imageList.get(0):null;
            ImageShow.loadFile(image,null!=item?item.existsPath():"");
        }

    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && !this.isFinishing()) {
            if (requestCode == RECODE_GET_PHOTO_CAMERA) {//处理拍照返回结果
                ArrayList<String> images = new ArrayList<>();
                File file = new File(imageFilePath);
                if (file.exists()) {
                    images.add(imageFilePath);
                }
                if (info.needCrop && images.size() == 1) {
                    goCrop(this, images.get(0), info.cropWidth, info.cropHeight, info.cropIsFixed);
                } else {
                    reImageSelect(images);
                }
            } else if (requestCode == 11) {
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra("data");
                    if (info.selectOnly) {
                        if (info.needCrop && null!=images&&images.size() == 1) {
                            goCrop(this, images.get(0), info.cropWidth, info.cropHeight, info.cropIsFixed);
                        } else {
                            reImageSelect(images);
                        }
                    } else {
                        reImageSelect(images);
                    }
                }
            } else if (requestCode == RECODE_GET_PHOTO_CROP) {
                String path = null != data ? data.getStringExtra("data") : "";
                if (StringUtils.isNotEmpty(path)) {
                    ArrayList<String> images = new ArrayList<>();
                    images.add(path);
                    setResult(RESULT_OK, new Intent().putStringArrayListExtra("data", images));
                    this.finish();
                } else {
                    UiUtil.init().toast(this, "裁剪失败");
                }
            }
        }
    }

    private ArrayList<String> selectImages;
    private void endSelect(ArrayList<String> image){
        selectImages=image;
        if (null!=selectImages&&selectImages.size()>0){
            UiUtil.init().showDialog(this,false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> dataList=new ArrayList<String>();
                    try {
                        for (String s : selectImages) {
                            try {
                                File oldFile=new File(s);
                                File newFile = ImageUtil.reduceImage(oldFile,1024);
                                if (null!=newFile&&newFile.exists()){
                                    dataList.add(newFile.getAbsolutePath());
                                    if (oldFile.getParent().equals(newFile.getParent())&&!oldFile.getAbsolutePath().equals(newFile.getAbsolutePath())){
                                        oldFile.delete();
                                    }
                                }else if(oldFile.exists()){
                                    dataList.add(oldFile.getAbsolutePath());
                                    if (null!=newFile&&oldFile.getParent().equals(newFile.getParent())&&!oldFile.getAbsolutePath().equals(newFile.getAbsolutePath())){
                                        newFile.delete();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                dataList.add(s);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(dataList,"select_list_success");
                }
            }).start();
        }else{
            finish();
        }
    }

    private void reImageSelect(ArrayList<String> image){
        selectImages=image;
        if (null!=selectImages&&selectImages.size()>0){
            UiUtil.init().showDialog(this,false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> reImages=new ArrayList<>();
                    for (String s : selectImages) {
                        String str="";
                        try {
                            str=new ReSizeImage(s).launch();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (StringUtils.isNotEmpty(str)){
                            reImages.add(str);
                        }else{
                            reImages.add(s);
                        }
                    }
                    EventBus.getDefault().post(reImages,"select_list_success");
                }
            }).start();
        }else{
            finish();
        }
    }

    @Subscriber(tag = "select_list_success")
    public void eventSelect(ArrayList<String> dataList){
        if (null!=OneCode.getConfig()){
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(OneCode.getConfig().getCachePath())));
            sendBroadcast(scanIntent);
        }
        UiUtil.init().cancelDialog();
        if (null!=dataList&&dataList.size()>0){
            setResult(RESULT_OK, new Intent().putStringArrayListExtra("data", dataList));
        }
        finish();
    }

}
