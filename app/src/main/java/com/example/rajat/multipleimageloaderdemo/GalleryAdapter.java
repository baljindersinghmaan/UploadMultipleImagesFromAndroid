package com.example.rajat.multipleimageloaderdemo;

import android.content.Context;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private Context ctx;
    private int pos;
    private LayoutInflater inflater;
    private ImageView ivGallery, deleteImg;
    private List<String> mArrayUri;

    private MyListener myListener;

    public GalleryAdapter(Context ctx, List<String> mArrayUri, MyListener myListener) {
        this.myListener = myListener;
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gv_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Glide.with(ctx).load(mArrayUri.get(i)).into(ivGallery);
    }


    @Override
    public int getItemCount() {
        return mArrayUri.size();
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {

        pos = position;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.gv_item, parent, false);

        ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);

        ivGallery.setImageURI(mArrayUri.get(position));

        return itemView;
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);
            deleteImg = itemView.findViewById(R.id.delete_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();


                    Toast.makeText(ctx, String.valueOf(getLayoutPosition()), Toast.LENGTH_SHORT).show();
                }
            });
            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();

                    Toast.makeText(ctx, String.valueOf(getLayoutPosition()), Toast.LENGTH_SHORT).show();
                    myListener.onClick(position,mArrayUri.get(getLayoutPosition()));


                }
            });
        }
    }

    public interface MyListener {
        void onClick(int position,String data);
    }
}
