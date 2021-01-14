package com.example.beauty;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private int index=1;

    private String url="https://smtmm.win";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        mToolBar=(Toolbar)findViewById(R.id.toolbar);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mRefresh=(SwipeRefreshLayout)findViewById(R.id.refresh);
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);

        setSupportActionBar(mToolBar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                coverInit();
            }
        }).start();

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Random random=new Random();
                index=random.nextInt(30);
                coverInit();
            }
        });
    }

    /*初始化home界面*/
    public void coverInit(){
        HttpUtil.sendOkHttpRequest(url+"/?page="+index, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomePage.this, "获取信息失败", Toast.LENGTH_SHORT).show();
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
        Elements Tags=doc.getElementsByTag("article");
        List<Cover> coverList=new ArrayList<>();
        for(Element Tag:Tags){
            if(!Tag.className().equals("excerpt excerpt-c5")) continue;
            Cover cover=new Cover();
            Pattern pattern=Pattern.compile("/static/images/\\S*.jpg");
            for(Element a:Tag.children()){
                if(a.className().equals("thumbnail")){
                    cover.setLink(url+a.attr("href"));
                    Matcher matcher=pattern.matcher(a.child(0).attr("style"));
                    if(matcher.find()) cover.setCoverUrl(url+matcher.group());
                    cover.setCoverName(a.nextElementSibling().child(1).text());
                    break;
                }
            }
            coverList.add(cover);
        }
        fillInCover(this,coverList);
        mRefresh.setRefreshing(false);
    }

    protected void fillInCover(final Context context, final List<Cover> coverList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final GridLayoutManager layoutManager=new GridLayoutManager(context,3);
                mRecyclerView.setLayoutManager(layoutManager);
                CoverAdapter adapter=new CoverAdapter(context,coverList);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
        }
        return true;
    }
}