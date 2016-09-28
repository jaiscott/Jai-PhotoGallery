package com.demo.photogallerytest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.photogallerytest.R;
import com.demo.photogallerytest.data.entities.AlbumResponse;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<AlbumResponse> albumResponses;
    private Context context;

    public AlbumAdapter(Context context, List<AlbumResponse> albumResponses) {
        this.albumResponses = albumResponses;
        this.context = context;
    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_album, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder holder, int position) {
        AlbumResponse albumResponse = albumResponses.get(position);
        holder.tv_android.setText(albumResponse.getTitle());
        holder.img_android.setBackgroundResource(R.drawable.defaultimage);
    }

    @Override
    public int getItemCount() {
        return albumResponses.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.album_title)
        TextView tv_android;
        @Bind(R.id.image_album)
        ImageView img_android;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
