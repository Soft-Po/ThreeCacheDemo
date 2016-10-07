package com.softpo.threecachedemo.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.softpo.threecachedemo.httputils.HttpUtils;

import okhttp3.OkHttpClient;


/**
 * Created by softpo on 2016/4/25.
 */
public class ImageAsynctask extends AsyncTask<String,Void,byte[]> {

    private DownloadImage mDownloadImage;
    private String mUrl;
    private OkHttpClient mOkHttpClient;

    //    通过构造方法对接口进行赋值
    public ImageAsynctask(OkHttpClient okHttpClient, DownloadImage downloadImage) {
        mDownloadImage = downloadImage;
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    protected byte[] doInBackground(String... params) {
//        获取图片地址
        mUrl = params[0];

        byte[] data = HttpUtils.getData(mUrl,mOkHttpClient);

        if (data != null) {
            return data;
        }
        return null;
    }

    @Override
    protected void onPostExecute(byte[] data) {//主线程执行
        super.onPostExecute(data);
        if (data != null) {
//            接口在此使用
                mDownloadImage.sendBitmap(data,mUrl);
        }

    }

//    接口回掉
    public interface DownloadImage{
        void sendBitmap(byte[] data, String path);
    }
}
