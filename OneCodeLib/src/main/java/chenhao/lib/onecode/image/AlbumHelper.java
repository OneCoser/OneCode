package chenhao.lib.onecode.image;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlbumHelper {
    Context context;
    ContentResolver cr;

    // 缩略图列表
    HashMap<String, String> thumbnailList = new HashMap<String, String>();
    // 专辑列表
    HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

    private static AlbumHelper instance;

    private AlbumHelper() {
    }

    public static AlbumHelper getNewHelper() {
        instance = new AlbumHelper();
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();
        }
    }

    /**
     * 得到缩略图
     */
    private void getThumbnail() {
        String[] projection =
                {Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA};
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getThumbnailColumnData(cursor);
    }

    /**
     * 从数据库中得到缩略图
     */
    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
            do {
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);
                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
    }

    /**
     * 是否创建了图片集
     */
    private boolean hasBuildImagesBucketList = false;

    /**
     * 得到图片集
     */
    private void buildImagesBucketList() {
        long startTime = System.currentTimeMillis();
        if (null==bucketList){
            bucketList=new HashMap<>();
        }else{
            bucketList.clear();
        }
        if (null==thumbnailList){
            thumbnailList=new HashMap<>();
        }else{
            thumbnailList.clear();
        }
        // 构造缩略图索引
        getThumbnail();

        // 构造相册索引
        String columns[] = new String[]
                {Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA,
                        Media.DISPLAY_NAME, Media.TITLE, Media.SIZE,
                        Media.BUCKET_DISPLAY_NAME, Media.DATE_ADDED};
        // 得到一个游标
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,
                Media.DATE_ADDED + " DESC");
        if (cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);
            // 获取图片总数
            int totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<Image>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                Image image = new Image();
                image.id = _id;
                image.path = path;
                image.thumbPath = thumbnailList.get(_id);
                bucket.imageList.add(image);
            } while (cur.moveToNext());
        }

        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
                    .next();
            ImageBucket bucket = entry.getValue();
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                Image image = bucket.imageList.get(i);
            }
        }
        hasBuildImagesBucketList = true;
        long endTime = System.currentTimeMillis();
    }

    /**
     * 得到图片集
     */
    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || !hasBuildImagesBucketList) {
            buildImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr.next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }

}
