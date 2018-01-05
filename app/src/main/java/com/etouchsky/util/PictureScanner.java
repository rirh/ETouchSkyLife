package com.etouchsky.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;


/**
 * 照片查看器
 */
public class PictureScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMs;  //媒体扫描类
    private File mFile;
    private Context context;
    private File folder;
    private File[] allFiles;

    public PictureScanner(Context context) {
        this.context = context;

    }

    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
        mMs.disconnect();
    }

    private void swap(File a[]) {
        int len = a.length;
        for (int i = 0; i < len / 2; i++) {
            File tmp = a[i];
            a[i] = a[len - 1 - i];
            a[len - 1 - i] = tmp;
        }
    }

    //开始扫描
    public boolean PictureStart(String path){
        File folder = new File(path);
        allFiles = folder.listFiles();
        swap(allFiles);
        if ( allFiles.length == 0) {
           return false;
        } else {
            mFile = allFiles[0];
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
            return true;
        }

    }


    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

}  