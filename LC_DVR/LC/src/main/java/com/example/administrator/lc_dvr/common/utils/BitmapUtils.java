package com.example.administrator.lc_dvr.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 *
 *
 */
public class BitmapUtils {

    private static final long MB = 1024 * 1024;


    /**
     * 获取SD卡根路径
     *
     * @return
     */
    public static String getSDPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }


    /**
     * 从本地path中获取bitmap，压缩后保存小图片到本地
     *
     * @param path    图片存放的路径
     *
     * @return 返回压缩后图片的存放路径
     */
    public static void saveBitmap(String path,String fileName) {
        String compressdPicPath = "";

//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
      /*  //★如果不压缩直接从path获取bitmap，这个bitmap会很大，下面在压缩文件到100kb时，会循环很多次，
        // ★而且会因为迟迟达不到100k，options一直在递减为负数，直接报错
        //★ 即使原图不是太大，options不会递减为负数，也会循环多次，UI会卡顿，所以不推荐不经过压缩，直接获取到bitmap
        Bitmap bitmap=BitmapFactory.decodeFile(path);*/
//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
        Bitmap bitmap = decodeSampledBitmapFromPath(path, 720, 1280);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /* options表示 如果不压缩是100，表示压缩率为0。如果是70，就表示压缩率是70，表示压缩30%; */
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        while (baos.toByteArray().length / 1024 > 200) {
        // 循环判断如果压缩后图片是否大于500kb继续压缩

            baos.reset();
            options -= 10;
            if (options < 11) {//为了防止图片大小一直达不到200kb，options一直在递减，当options<0时，下面的方法会报错
                // 也就是说即使达不到200kb，也就压缩到10了
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                break;
            }
            // 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        File dirFile = new File(BitmapUtils.getSDPath() + "/VOC/Cache/");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File f = new File(BitmapUtils.getSDPath() + "/VOC/Cache/" + fileName + ".png");
        if (f.exists()) {
            f.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(baos.toByteArray());
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据图片要显示的宽和高，对图片进行压缩，避免OOM
     *
     * @param path
     *
     *  直接显示720 和 480 大小的
     * @return
     */
    public static Bitmap decodeSampledBitmapFromPath(String path) {

//      获取图片的宽和高，并不把他加载到内存当中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, 720, 480);
//      使用获取到的inSampleSize再次解析图片(此时options里已经含有压缩比 options.inSampleSize，再次解析会得到压缩后的图片，不会oom了 )
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;

    }

    /**
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private static Bitmap decodeSampledBitmapFromPath(String path,int width,int height) {

//      获取图片的宽和高，并不把他加载到内存当中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);
//      使用获取到的inSampleSize再次解析图片(此时options里已经含有压缩比 options.inSampleSize，再次解析会得到压缩后的图片，不会oom了 )
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;

    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param options
     * @param reqWidth  要显示的imageview的宽度
     * @param reqHeight 要显示的imageview的高度
     * @return
     * @compressExpand 这个值是为了像预览图片这样的需求，他要比所要显示的imageview高宽要大一点，放大才能清晰
     */
    private static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width >= reqWidth || height >= reqHeight) {

            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(width * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);

        }

        return inSampleSize;
    }









}
