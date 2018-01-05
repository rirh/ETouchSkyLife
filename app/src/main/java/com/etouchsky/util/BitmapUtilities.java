package com.etouchsky.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapUtilities {

    public BitmapUtilities() {
        // TODO Auto-generated constructor stub
    }

    public static Bitmap getBitmapThumbnail(String path,int width,int height){
        Bitmap bitmap = null;
        //这里可以按比例缩小图片：
            /*BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;//宽和高都是原来的1/4
            bitmap = BitmapFactory.decodeFile(path, opts); */

            /*进一步的，
                如何设置恰当的inSampleSize是解决该问题的关键之一。BitmapFactory.Options提供了另一个成员inJustDecodeBounds。
               设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。
               有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。*/
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = Math.max((int)(opts.outHeight / (float) height), (int)(opts.outWidth / (float) width));
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }

    public static Bitmap getBitmapThumbnail(String url,int width){
        Bitmap bmp=null;
        try {
            FileInputStream is = new FileInputStream(url);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        if(bmp != null ){
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            int height=width*bmpHeight/bmpWidth;
            if(width != 0 && height !=0){
                Matrix matrix = new Matrix();
                float scaleWidth = ((float) width / bmpWidth);
                float scaleHeight = ((float) height / bmpHeight);
                matrix.postScale(scaleWidth, scaleHeight);
                bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
            }else{
                bitmap = bmp;
            }
        }
        return bitmap;
    }
    public static Bitmap getBitmapThumbnail1(String myJpgPath){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        bitmap = BitmapFactory.decodeFile(myJpgPath, options);
        return bitmap;
    }

}
