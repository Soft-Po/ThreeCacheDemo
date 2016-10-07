package com.softpo.threecachedemo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.softpo.threecachedemo.R;
import com.softpo.threecachedemo.asynctasks.ImageAsynctask;
import com.softpo.threecachedemo.cache.MyLruCache;
import com.softpo.threecachedemo.sdutils.ExternalStorageUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by softpo on 2016/4/25.
 */
public class MyAdapter extends BaseAdapter {
    private HashMap<String, SoftReference<byte[]>> mHashMap;
    private Context mContext;
    private List<String> mList;
    private final MyLruCache mLruCache;
    private OkHttpClient mOkHttpClient;
    //    Alt + insert
    public MyAdapter(Context context, List<String> list, OkHttpClient okHttpClient) {
        mContext = context;
        mList = list;
//        构造方法中创建LruCache
        int size = (int) (Runtime.getRuntime().maxMemory()/8);

        mHashMap = new HashMap<String, SoftReference<byte[]>>();

        Log.d("flag","------------------>size: "+size*1.0f/1024/1024);

        mLruCache = new MyLruCache(size, mHashMap);

        this.mOkHttpClient = okHttpClient;

    }

    @Override
    public int getCount() {
        int ret = 0;
        if (mList != null) {
            ret = mList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("flag","---------->postion: "+position);
        View ret = null;
        if (convertView != null) {
            ret = convertView;
        }else {
            ret = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
        }
//////////////////////////////////////////////////////////////////////////////////
        ViewHolder holder = (ViewHolder) ret.getTag();

        if (holder == null) {
            holder = new ViewHolder();

            holder.mImageView = ((ImageView) ret.findViewById(R.id.showImg));
            ret.setTag(holder);
        }
        /////////////////////////////////////////////////////////////////////////
//        进行赋值
//        不通过缓存解决图片错位问题
//        1、设置Tag
//        2、图片的网址作为Tag
        String imagePath = mList.get(position);
        holder.mImageView.setTag(imagePath);
//        获取缓存中数据
        Bitmap bitmap = getCacheBitmap(imagePath);
        if (bitmap != null) {
            holder.mImageView.setImageBitmap(bitmap);
        }else {//缓存中没有数据，从网络获取
            getNetBitmap(imagePath,holder,mOkHttpClient);
        }
        return ret;
    }

    private void getNetBitmap(final String imagePath, final ViewHolder holder, OkHttpClient okHttpClient) {
        new ImageAsynctask(okHttpClient,new ImageAsynctask.DownloadImage() {
            @Override
            public void sendBitmap(byte[] data,String path) {//此处的bitmap是onPostExcute中下载的数据
//                回掉中获取刚才设置的tag
                String tag = (String) holder.mImageView.getTag();
                if(tag.equals(path)){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                    holder.mImageView.setImageBitmap(bitmap);
//                做SD卡缓存
                    String filePath = imagePath.substring(imagePath.lastIndexOf("/")+1);
                    ExternalStorageUtils.writeDataToRoot(mContext,filePath,data);
                    Log.d("flag","---------->缓存到SD卡"+filePath);

//                做内存缓存
                    mLruCache.put(imagePath,data);
                }
            }
        }).execute(imagePath);
    }
    private Bitmap getCacheBitmap(String imagePath) {
        byte[] data = mLruCache.get(imagePath);
        if (data != null&&data.length>0) {//有数据直接赋值
            Log.d("flag","---------------->从LruCache获取数据");
            return BitmapFactory.decodeByteArray(data,0,data.length);
        }else {//软引用没有查找内存卡
                String filePath = imagePath.substring(imagePath.lastIndexOf("/") + 1);


            byte[] bytes = ExternalStorageUtils.readDataFromRoot(mContext,filePath);

            if (bytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                mLruCache.put(imagePath,bytes);
                Log.d("flag", "----------->从Sd卡中获取数据 "+filePath);

                    return bitmap;
                }
            }
        return null;
    }
    private class ViewHolder{
        private ImageView mImageView;
    }
}
