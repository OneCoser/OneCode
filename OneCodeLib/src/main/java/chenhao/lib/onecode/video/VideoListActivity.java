package chenhao.lib.onecode.video;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import chenhao.lib.onecode.R;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import chenhao.lib.onecode.base.BaseActivity;
import chenhao.lib.onecode.utils.FileUtils;
import chenhao.lib.onecode.utils.StringUtils;
import chenhao.lib.onecode.utils.UiUtil;
import chenhao.lib.onecode.video.ijk.CommonVideoView;
import chenhao.lib.onecode.view.TitleView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

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

    private Video get;
    private GridView videosShow;
    private VideoShowAdapter showAdapter;
    private AbsListView.LayoutParams params;
    private View video_player_layout, video_player_action;
    private CommonVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onecode_activity_video_get);
        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setOnTitleViewAction(new TitleView.OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action == TitleView.ACTION_LEFT_CLICK) {
                    onBackPressed();
                }
            }
        });
        video_player_layout = findViewById(R.id.video_player_layout);
        video_player_action = findViewById(R.id.video_player_action);
        videoView = (CommonVideoView) findViewById(R.id.video_player);
        video_player_layout.setOnClickListener(this);
        findViewById(R.id.video_player_close).setOnClickListener(this);
        findViewById(R.id.video_player_submit).setOnClickListener(this);
        videosShow = (GridView) findViewById(R.id.video_get_list);
        videosShow.setOnItemClickListener(this);
        int w = getResources().getDisplayMetrics().widthPixels;
        float dp = getResources().getDisplayMetrics().density;
        int itemWH = (w - (int) (20 * dp)) / 3;
        params = new AbsListView.LayoutParams(itemWH, itemWH);
        changeFull(false);
        loadData(true);
    }

    private static final String EVENT_DATA_SUCCESS = "event_video_get_data_success";

    private void loadData(boolean showDialog) {
        if (showDialog) {
            UiUtil.init().showDialog(this, true);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Video> list = new ArrayList<>();
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
                            list.add(get);
                        }
                    }
                    cursor.close();
                }
                EventBus.getDefault().post(list, EVENT_DATA_SUCCESS);
            }
        }).start();
    }

    @Subscriber(tag = EVENT_DATA_SUCCESS)
    public void onDataSuccess(List<Video> videos) {
        showAdapter = new VideoShowAdapter(videos);
        videosShow.setAdapter(showAdapter);
        UiUtil.init().cancelDialog();
    }

    private class VideoShowAdapter extends BaseAdapter {

        List<Video> list;

        public VideoShowAdapter(List<Video> videos) {
            list = new ArrayList<>();
            if (null != videos && videos.size() > 0) {
                list.addAll(videos);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Video getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ShowItem item = null;
            if (null == v || null == v.getTag() || !(v.getTag() instanceof ShowItem)) {
                item = new ShowItem();
                v = View.inflate(VideoListActivity.this, R.layout.onecode_item_video_get, null);
                item.name = (TextView) v.findViewById(R.id.item_video_get_name);
                item.thumb = (ImageView) v.findViewById(R.id.item_video_get_thumb);
                v.setLayoutParams(params);
                v.setTag(item);
            } else {
                item = (ShowItem) v.getTag();
            }
            Video get = list.get(position);
            item.name.setText(get.name);
//            if (null != get.thumb) {
//                item.thumb.setImageBitmap(get.thumb);
//            } else
            if (StringUtils.isNotEmpty(get.thumbPath)) {
                item.thumb.setImageBitmap(BitmapFactory.decodeFile(get.thumbPath));
            } else {
                item.thumb.setImageResource(R.drawable.default_image_load);
            }
            return v;
        }
    }

    private class ShowItem {
        public TextView name;
        public ImageView thumb;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.video_player_close){
            stop();
        }else if(v.getId()==R.id.video_player_submit){
            stop();
            if (null != get) {
                setResult(RESULT_OK, new Intent().putExtra("video", get));
                finish();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != showAdapter && position >= 0 && position < showAdapter.getCount()) {
            get = showAdapter.getItem(position);
            play();
        }
    }

    private void play() {
        if (null != get) {
            videoView.stop();
            if (video_player_layout.getVisibility() != View.VISIBLE) {
                video_player_layout.setVisibility(View.VISIBLE);
            }
            videoView.setData(get.path, "file://" + get.thumbPath);
            videoView.setVideoViewListener(videoViewListener);
            video_player_action.setVisibility(View.VISIBLE);
        } else {
            stop();
        }
    }

    private boolean stop() {
        if (null != videoView) {
            videoView.stop();
        }
        if (video_player_layout.getVisibility() != View.GONE) {
            video_player_layout.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isFull) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (!stop()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeFull(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private boolean isFull;

    public void changeFull(boolean full) {
        isFull = full;
        WindowManager.LayoutParams fullParams = getWindow().getAttributes();
        if (isFull) {
            fullParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(fullParams);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            fullParams.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(fullParams);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        videoView.setFullIcon(isFull);
    }

    private CommonVideoView.OnCommonVideoViewListener videoViewListener = new CommonVideoView.OnCommonVideoViewListener() {
        @Override
        public void onShowController(boolean show) {
            if (videoView.hasUrl()) {
                video_player_action.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void pauseOrStart(boolean isStart) {

        }

        @Override
        public void onCompletion(IMediaPlayer mp) {

        }

        @Override
        public void onClickFull() {
            setRequestedOrientation(isFull ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (null != videoView) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void reLoad(int status) {

    }
}
