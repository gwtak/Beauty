package com.example.beauty;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CoverAdapter extends RecyclerView.Adapter<CoverAdapter.ViewHolder>{
    Context mContext;
    private List<Cover> mCoverList;
    public CoverAdapter(Context context,List<Cover> CoverList){
        mContext=context;
        mCoverList=CoverList;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView coverImage;
        TextView coverName;
        public ViewHolder(View view){
            super(view);
            coverImage=(ImageView)view.findViewById(R.id.cover_image);
            coverName=(TextView)view.findViewById(R.id.cover_name);
        }
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cover_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Cover cover=mCoverList.get(position);
                Intent intent=new Intent(mContext,ActivityImage.class);
                intent.putExtra("Link",cover.getLink());
                intent.putExtra("Name",cover.getCoverName());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }
    /*
    * 适配器绑定cover名字
    * 使用glide绑定链接，显示图片
    */
    public void onBindViewHolder(ViewHolder holder,int position){
        Cover cover=mCoverList.get(position);
        holder.coverName.setText(cover.getCoverName());
        Glide.with(mContext).load(cover.getCoverUrl()).into(holder.coverImage);
    }
    public int getItemCount(){
        return mCoverList.size();
    }
}
