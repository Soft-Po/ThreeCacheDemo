package com.softpo.threecachedemo.httputils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by softpo on 2016/4/25.
 */
public class HttpUtils {

////    给一个静态的联网请求方
//    public static byte[] getData(String path){
//        byte [] ret = null;
////        开启联网请求
//
//        InputStream in = null;
//
//        ByteArrayOutputStream baos = null;
//        try {
//            URL url = new URL(path);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
////            设置
//            conn.setConnectTimeout(5000);
//
//            conn.setRequestMethod("GET");
//
//            conn.setDoInput(true);
//
//            conn.connect();
//
////            判断是否连接成功
//            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
//                in = conn.getInputStream();
//               baos= new ByteArrayOutputStream();
//                byte[] buff = new byte[1024];
//                while (true){
//                    int len = in.read(buff);
//                    if(len ==-1){
//                        break;
//                    }
//                    baos.write(buff,0,len);
//                }
//                ret = baos.toByteArray();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                in.close();
//                baos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return  ret;
//    }


    //    给一个静态的联网请求方
    public static byte[] getData(String path, OkHttpClient okHttpClient){
        byte [] ret = null;
//        开启联网请求
        Request.Builder builder = new Request.Builder();

        builder.url(path);


        try {
            Response execute = okHttpClient.newCall(builder.build()).execute();

            if(execute.isSuccessful()){
                ret = execute.body().bytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  ret;
    }

}
