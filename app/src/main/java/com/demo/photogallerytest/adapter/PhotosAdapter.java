package com.demo.photogallerytest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.demo.photogallerytest.R;
import com.demo.photogallerytest.data.entities.PhotosResponse;
import com.demo.photogallerytest.utils.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private List<PhotosResponse> photosResponses;
    private Context context;

    public PhotosAdapter(Context context, List<PhotosResponse> photosResponses) {
        this.photosResponses = photosResponses;
        this.context = context;
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_photos, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotosAdapter.ViewHolder holder, int position) {
        PhotosResponse photosResponse = photosResponses.get(position);
        ImageLoader.loadProductImage(holder.imageView, photosResponse.getThumbnailUrl());
    }

    @Override
    public int getItemCount() {
        return photosResponses.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_photos)
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

