package com.example.beauty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mUrlList;

    public ImageAdapter(Context context,List<String> UrlList){
        mContext=context;
        mUrlList=UrlList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView Image;
        public ViewHolder(View view){
            super(view);
            Image=(ImageView)view.findViewById(R.id.image);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                String url=mUrlList.get(position);
                Intent intent=new Intent(mContext,BigImage.class);
                intent.putExtra("Image",url);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position){
        String url=mUrlList.get(position);
        Glide.with(mContext).load(url).into(holder.Image);
    }

    public int getItemCount(){
        return mUrlList.size();
    }
}
