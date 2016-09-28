package com.demo.photogallerytest.data.api;

import com.demo.photogallerytest.data.entities.AlbumResponse;
import com.demo.photogallerytest.data.entities.PhotosResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import rx.Observable;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public interface GalleryApi {
    @Headers("Cache-Control: public, max-age=640000, s-maxage=640000 , max-stale=2419200")
    @GET("albums")
    Observable<List<AlbumResponse>> getAlbumResponse();

    @Headers("Cache-Control: public, max-age=640000, s-maxage=640000 , max-stale=2419200")
    @GET("photos")
    Observable<List<PhotosResponse>> getPhotoResponse();
}
