package com.softpo.threecachedemo.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by softpo on 2016/4/25.
 */
public class MyLruCache extends LruCache<String,byte[]> {

//    内存的双缓存
//    软引用
    private HashMap<String,SoftReference<byte[]>> mBitmapSoftReference;
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MyLruCache(int maxSize) {
        super(maxSize);
    }

    public MyLruCache(int maxSize,HashMap<String,SoftReference<byte[]>> mBitmapSoftReference ) {
        super(maxSize);
        this.mBitmapSoftReference = mBitmapSoftReference;
    }
    @Override
    protected int sizeOf(String key, byte[] value) {
        int ret = 0;

        if (value != null) {//计算放入LruCache中图片的大小
            ret = value.length;
            Log.d("flag","--------------->存入LruCache大小： "+ret*1.0f/1024/1024);
        }
        return ret;
    }

//    最近最少使用算法
//    LruCache内存不足，删除数据
//    LinkedList中数据，删除最末尾的数据
//    LruCache一般情况只占内存8分之一
//    还有一些内存没有在使用，软引用（一旦内存不足，GC会回收）
//    额外的内存暂时交给软引用

//    内存双（LruCache，软引用）缓存

//    软引用就要在LruCache移除数据时，放到软引用中

//    以后使用的时候，先查找，LruCache
//    再查找软引用

    @Override
    protected void entryRemoved(boolean evicted, String key, byte[] oldValue, byte[] newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if(evicted){//根据最近最少原则删除的图片放入软引用
            SoftReference<byte[]> softReference = new SoftReference<byte[]>(oldValue);
            if (mBitmapSoftReference != null) {
                Log.d("flag","----------------->从LruCache中移出数据");
                mBitmapSoftReference.put(key,softReference);
            }
        }
    }

    @Override
    protected byte[] create(String key) {
        byte[] bitmap = super.create(key);
        if (bitmap == null) {
            //从软引用中获取数据
            SoftReference<byte[]> softReference = mBitmapSoftReference.get(key);
            if (softReference != null) {
                Log.d("flag","--------------->获取软引用中的数据");
                bitmap = softReference.get();
                mBitmapSoftReference.remove(key);
                this.put(key,bitmap);
            }
        }
        return bitmap;
    }
}
