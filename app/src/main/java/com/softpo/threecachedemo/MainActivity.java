package com.softpo.threecachedemo;

import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.softpo.threecachedemo.adapters.MyAdapter;
import com.softpo.threecachedemo.datas.ImageUrl;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

//        获取模拟器或者手机分配给每个App内存大小
        int memoryClass = manager.getMemoryClass();

        mOkHttpClient = new OkHttpClient();

        Toast.makeText(this,"MemorySize: "+memoryClass,Toast.LENGTH_LONG).show();


        initView();

        initData();
    }

    private void initData() {

        List<String> list = new ArrayList<>();

        String[] imageurls = ImageUrl.imageurls;

        for (int i = 0; i < imageurls.length; i++) {
            list.add(imageurls[i]);
        }
        MyAdapter adapter = new MyAdapter(this,list,mOkHttpClient);

        mListView.setAdapter(adapter);
    }

    private void initView() {
        mListView = ((ListView) findViewById(R.id.listView));
    }
}
