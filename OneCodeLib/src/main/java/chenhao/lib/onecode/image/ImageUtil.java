package chenhao.lib.onecode.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.utils.StringUtils;

public class ImageUtil {

    /**
     * 保存图片
     *
     * @param mBitmap
     * @param path
     * @return
     */
    public static String saveMyBitmap(Bitmap mBitmap, String path, int quality) {
        if (mBitmap == null) {
            return null;
        }
        File f = null;
        if (path.startsWith("/")) {
            f = new File(path);
        } else if(null!= OneCode.getConfig()){
            f = new File(OneCode.getConfig().getCachePath()+path);
        }else{
            return "";
        }
        FileOutputStream fOut = null;
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError out) {
            out.printStackTrace();
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                }
            }
        }
        if (f != null) {
            return f.getPath();
        } else {
            return path;
        }
    }

    /**
     * 取图片压缩
     *
     * @param fileName
     * @param maxSize
     * @param limit    为true时，最大边为maxSize，flase时，最小边为maxSize；
     * @return
     */
    public static Bitmap decodeFile(String fileName, int maxSize, boolean limit) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        File f = null;
        if (fileName.startsWith("/")) {
            f = new File(fileName);
        } else if(null!=OneCode.getConfig()){
            f = new File(OneCode.getConfig().getCachePath()+fileName);
        }else {
            return null;
        }
        if (!f.exists()) {
            return null;
        }
        return decodeFile(f.getAbsolutePath(),maxSize,limit,readPictureDegree(f.getAbsolutePath()));
    }

    public static Bitmap decodeFile(String path, int maxSize, boolean limit, int degree) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            if (maxSize == 0 || maxSize > 1500) {
                maxSize = 1500;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            int scale = 1;
            if (o.outHeight <= 0 || o.outWidth <= 0) {
                o.outHeight = o.outWidth = 750;
            }
            int maxNumOfPixels = 750 * 1500;
            if (Math.max(o.outWidth, o.outHeight) / Math.min(o.outWidth, o.outHeight) > 2) {
                maxNumOfPixels = 750 * (750 * Math.max(o.outWidth, o.outHeight) / Math.min(o.outWidth, o.outHeight));
            }
            scale = computeSampleSize(o, maxSize, maxNumOfPixels, limit);
            o2.inSampleSize = scale;
            o2.inJustDecodeBounds = false;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            if (degree > 0) {
                b = rotaingImageView(degree, b);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }


    /**
     * 计算图片压缩比例
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @param limit
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels, boolean limit) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels, limit);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels, boolean limit) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.round(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (limit) ? (int) Math.max(Math.round((float) w / (float) minSideLength), Math.round((float) h / (float) minSideLength)) : (int) Math.min(Math.floor((float) w / (float) minSideLength), Math.floor((float) h / (float) minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
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


    /**
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null || angle == 0) {
            return bitmap;
        }
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static int[] getImageSizeByFile(File file) {
        int[] size = new int[]{0, 0};
        if (null!=file&&file.exists()){
            try {
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(),newOpts);//此时返回bm为空
                newOpts.inJustDecodeBounds = false;
                size[0] = newOpts.outWidth;
                size[1] = newOpts.outHeight;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    public static File reduceImage(File file, int checkSize){
        if (null==file||!file.exists()||null==OneCode.getConfig()){
            return file;
        }
        try{
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),newOpts);//此时返回bm为空
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            if (checkSize<=0||w>=checkSize||h>=checkSize){
                float hh = 1280f;//这里设置高度
                float ww = 768f;//这里设置宽度
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (newOpts.outWidth / ww);
                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;//设置缩放比例
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), newOpts);
                int degree=readPictureDegree(file.getAbsolutePath());
                if (degree>0){
                    bitmap=rotaingImageView(degree,bitmap);
                }
                String newPath= OneCode.getConfig().getCachePath()+"re_"+file.getName();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newPath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                return new File(newPath);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (OutOfMemoryError e){
            e.printStackTrace();
            System.gc();
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }

    public static void checkDegree(String path) {
        int degree = 0;
        if (StringUtils.isNotEmpty(path)){
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
        }
        if (degree > 0) {
            try {
                Bitmap baseBitmap = BitmapFactory.decodeFile(path);
                if (null != baseBitmap) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);
                    Bitmap resizedBitmap = Bitmap.createBitmap(baseBitmap, 0, 0,
                            baseBitmap.getWidth(), baseBitmap.getHeight(), matrix, true);
                    baseBitmap.recycle();
                    if (null!=resizedBitmap){
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        resizedBitmap.recycle();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
