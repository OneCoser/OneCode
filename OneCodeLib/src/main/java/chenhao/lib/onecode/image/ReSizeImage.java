package chenhao.lib.onecode.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.utils.Clog;
import chenhao.lib.onecode.utils.FileUtils;

/**
 * Created by onecode on 16/8/30.
 * 压缩图片
 */
public class ReSizeImage {

    private File mFile;

    public ReSizeImage(String src) {
        mFile=new File(src);
    }

    public String launch() {
        String newPath="";
        boolean needReImage=false;
        try {
            long fileSize= FileUtils.byteCountToKB(mFile.length());
            Clog.i("oldFile："+fileSize+"KB---src："+mFile.getAbsolutePath());
            needReImage=fileSize>400;
            if (needReImage){
                newPath=thirdCompress();
            }
        }catch (Exception e){
            e.printStackTrace();
            needReImage=false;
        }
        return newPath;
    }

    private String thirdCompress() {

        double s;

        int a = getImageSpinAngle(mFile.getAbsolutePath());
        int[] imgSize = getImageSize(mFile.getAbsolutePath());
        int i = imgSize[0];
        int j = imgSize[1];
        int k = i % 2 == 1 ? i + 1 : i;
        int l = j % 2 == 1 ? j + 1 : j;

        i = k > l ? l : k;
        j = k > l ? k : l;

        double c = ((double) i / j);

        if (c <= 1 && c > 0.5625) {
            if (j < 1664) {
                s = (i * j) / Math.pow(1664, 2) * 150;
                s = s < 60 ? 60 : s;
            } else if (j >= 1664 && j < 4990) {
                k = i / 2;
                l = j / 2;
                s = (k * l) / Math.pow(2495, 2) * 300;
                s = s < 60 ? 60 : s;
            } else if (j >= 4990 && j < 10240) {
                k = i / 4;
                l = j / 4;
                s = (k * l) / Math.pow(2560, 2) * 300;
                s = s < 100 ? 100 : s;
            } else {
                int multiple = j / 1280;
                k = i / multiple;
                l = j / multiple;
                s = (k * l) / Math.pow(2560, 2) * 300;
                s = s < 100 ? 100 : s;
            }
        } else if (c <= 0.5625 && c > 0.5) {
            int multiple = j / 1280;
            k = i / multiple;
            l = j / multiple;
            s = (k * l) / (1440.0 * 2560.0) * 200;
            s = s < 100 ? 100 : s;
        } else {
            int multiple = (int) Math.ceil(j / (1280.0 / c));
            k = i / multiple;
            l = j / multiple;
            s = ((k * l) / (1280.0 * (1280 / c))) * 500;
            s = s < 100 ? 100 : s;
        }

        return compress(mFile.getAbsolutePath(), k, l, a, (long) s);
    }

    private int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }

    private int getImageSpinAngle(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private String compress(String largeImagePath, int width, int height, int angle, long size) {
        Bitmap thbBitmap = compress(largeImagePath, width, height);

        thbBitmap = rotatingImage(angle, thbBitmap);

        return saveImage(thbBitmap, size);
    }

    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        //rotate image
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //create a new image
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String saveImage(Bitmap bitmap, long size) {
        String newPath="";
        if (null== OneCode.getConfig()){
            return "";
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);

        while (stream.toByteArray().length / 1024 > size) {
            stream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }
        try {
            File newFile=new File(OneCode.getConfig().getCachePath() + "re_" + mFile.getName());
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();

            if (newFile.exists()){
                newPath=newFile.getAbsolutePath();
                if (null!=mFile&&mFile.getParent().equals(newFile.getParent())&&!mFile.getAbsolutePath().equals(newFile.getAbsolutePath())){
                    mFile.delete();
                }
                long fileSize= FileUtils.byteCountToKB(newFile.length());
                Clog.i("newFile："+fileSize+"KB---src："+newPath);
            }else if(null!=mFile&&mFile.exists()){
                if (mFile.getParent().equals(newFile.getParent())&&!mFile.getAbsolutePath().equals(newFile.getAbsolutePath())){
                    newFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            newPath="";
        }
        return newPath;
    }

}
