package chenhao.lib.onecode.video;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.base.BaseViewHolder;
import chenhao.lib.onecode.base.RefreshBaseActivity;
import chenhao.lib.onecode.utils.FileUtils;
import chenhao.lib.onecode.utils.ImageShow;
import chenhao.lib.onecode.utils.LayoutManagerUtil;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.view.AlertItem;
import chenhao.lib.onecode.view.TitleView;

public class VideoListActivity extends RefreshBaseActivity<Video>{

    public static final int RECODE_GET_VIDEO = 66;
    public static void get(Activity a){
        if (null!=a){
            Intent intent=new Intent(a,VideoListActivity.class);
            a.startActivityForResult(intent,RECODE_GET_VIDEO);
        }
    }

    @Override
    public int getStatusBarColor() {
        return Color.BLACK;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return LayoutManagerUtil.getGrid(this,3);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleView titleView=findV(R.id.title_view);
        titleView.setBackgroundColor(Color.parseColor("#161616"));
        getRefreshView().setBackgroundColor(Color.parseColor("#333333"));
        getRefreshViewLayout().setBackgroundColor(Color.parseColor("#333333"));
        titleView.setTextIcon("视频","","",R.drawable.onecode_icon_back_w,0);
        titleView.getTitleTextView().setTextColor(Color.WHITE);
        titleView.setShow(TitleView.SHOW_ICON, TitleView.SHOW_NONE);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action== TitleView.ACTION_LEFT_CLICK){
                    onBackPressed();
                }
            }
        });
        loadData(false,false);
    }

    @Override
    public void loadData(boolean getMore, boolean isUser) {
        super.loadData(getMore, isUser);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Video> data = new ArrayList<>();
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DATE_ADDED + " desc");
                if (null != cursor) {
                    String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        Video get = new Video();
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        get.name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        get.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        try {
                            get.size = FileUtils.sizeOf(new File(get.path));
                        } catch (Exception e) {
                            e.printStackTrace();
                            get.size = 0;
                        }
                        String sizeStr = "";
                        try {
                            sizeStr = FileUtils.byteCountToDisplaySize(get.size);
                        } catch (Exception e) {
                            e.printStackTrace();
                            sizeStr = "";
                        }
                        if (StringUtils.isNotEmpty(sizeStr)) {
                            get.name += String.format("（%s）", sizeStr);
                        }
                        //获取视频缩略图文件地址
                        String whereClause = MediaStore.Video.Thumbnails.VIDEO_ID + " = " + id;
                        Cursor thumbCursor = cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projection, whereClause, null, null);
                        if (null != thumbCursor) {
                            if (thumbCursor.moveToFirst()) {
                                get.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                            }
                            thumbCursor.close();
                        }
                        //获取视频缩略图
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inDither = false;
//                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                        get.thumb = MediaStore.Video.Thumbnails.getThumbnail(cr,
//                                get.id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                        if (get.size > 0) {
                            data.add(get);
                        }
                    }
                    cursor.close();
                }
                EventBus.getDefault().post(data, EVENT_DATA_SUCCESS);
            }
        }).start();
    }

    private final String EVENT_DATA_SUCCESS = "event_video_list_data_success";
    @Subscriber(tag = EVENT_DATA_SUCCESS)
    public void onDataSuccess(List<Video> videos) {
        onDataSuccess(videos,SYSTEM_STATUS_NULL_DATA,false);
    }

    @Override
    protected BaseViewHolder<Video> getItem(int viewType) {
        return new ItemVideo();
    }

    public class ItemVideo extends BaseViewHolder<Video>{

        ImageView image;
        TextView name;

        Video item;

        public ItemVideo() {
            super(View.inflate(VideoListActivity.this,R.layout.onecode_item_video_list,null));
            itemView.setLayoutParams(new RecyclerView.LayoutParams(screenW/3,screenW/3));
            image=findV(itemView,R.id.item_video_get_thumb);
            name=findV(itemView,R.id.item_video_get_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null!=item){
                        new AlertItem.Builder(VideoListActivity.this)
                                .setItems(new String[]{"播放视频", "确定选择", "取消"}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i==0){
                                            if (null== OneCode.getConfig()||!OneCode.getConfig().goVideoPlay(item)){
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(Uri.parse(item.path),"video/mp4");
                                                VideoListActivity.this.startActivity(intent);
                                            }
                                        }else if(i==1){
                                            setResult(RESULT_OK, new Intent().putExtra("data", item));
                                            finish();
                                        }
                                    }
                                }).createShow();
                    }
                }
            });
        }

        @Override
        public void initView(Video video, int position) {
            ImageShow.loadFile(image,video.thumbPath);
            name.setText(video.name);
            item=video;
        }
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }
}
