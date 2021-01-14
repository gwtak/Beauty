package com.example.beauty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ActivityImage extends AppCompatActivity {

    private RecyclerView mImageBed;
    private SwipeRefreshLayout mRefresh;
    private FloatingActionButton mFloatButton;
    private List<String> UrlList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image);

        mImageBed=(RecyclerView)findViewById(R.id.image_bed);
        mRefresh=(SwipeRefreshLayout)findViewById(R.id.refresh_image);
        mFloatButton=(FloatingActionButton)findViewById(R.id.download_all);

        Intent intent=getIntent();
        final String url=intent.getStringExtra("Link");
        final String name=intent.getStringExtra("Name");

        //if(ContextCompat.checkSelfPermission(this,"android.permission.WRITE_EXTERNAL_STORAGE")!=PERMISSION_GRANTED) shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageInit(url);
            }
        }).start();

        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file=new File("/storage/emulated/0/Pictures/"+name);
                        if(!file.exists()) file.mkdirs();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityImage.this, name+"开始下载", Toast.LENGTH_SHORT).show();
                            }
                        });
                        for(int i=0;i<UrlList.size();i++){
                            try{
                                URL url=new URL(UrlList.get(i));
                                //Log.d("Message:",UrlList.get(i));
                                InputStream in=url.openStream();
                                Bitmap bmp= BitmapFactory.decodeStream(in);
                                in.close();
                                file=new File("/storage/emulated/0/Pictures/"+name+"/"+i+".jpg");
                                //Log.d("Message:",file.getPath());
                                //Log.d("Message:",file.getName());
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                                bos.flush();
                                bos.close();
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityImage.this, name+"下载完毕", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    protected void ImageInit(String url){
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityImage.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String html=response.body().string();
                //Log.d("Message:",html);
                HtmlParser(html);
            }
        });
    }

    /*解析html*/
    protected void HtmlParser(String html){
        Document doc= Jsoup.parse(html);
        Elements Tags=doc.getElementsByTag("img");
        UrlList.clear();
        for(Element Tag:Tags){
            //Log.d("Message:",Tag.toString());
            Pattern pattern=Pattern.compile("/static/images/\\S*\\.jpg");
            Matcher matcher=pattern.matcher(Tag.attr("data-original"));
            if(matcher.find()) UrlList.add("https://smtmm.win"+matcher.group());
        }
        fillInImage(this,UrlList);
    }

    protected void fillInImage(final Context context, final List<String> UrlList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final GridLayoutManager layoutManager=new GridLayoutManager(context,2);
                mImageBed.setLayoutManager(layoutManager);
                ImageAdapter adapter=new ImageAdapter(context,UrlList);
                mImageBed.setAdapter(adapter);
            }
        });
    }
}