package com.demo.photogallerytest;

import android.content.Context;

import com.demo.photogallerytest.data.DataModule;
import com.demo.photogallerytest.data.api.GalleryApi;
import com.demo.photogallerytest.ui.gallery.AlbumActivity;
import com.demo.photogallerytest.ui.gallery.PhotoActivity;
import com.google.gson.Gson;


import javax.inject.Singleton;

import dagger.Component;


import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */

@Singleton
@Component(modules = {DataModule.class, AppModule.class})
public interface AppComponent {

    Context provideAppContext();

    Gson provideGson();

    OkHttpClient provideOkHttpClient();

    //Interceptor provideOfflineCacheInterceptor();

    //Interceptor provideCacheInterceptor();

    //HttpLoggingInterceptor provideHttpLoggingInterceptor();

    Retrofit provideRetrofit();

    HttpUrl provideBaseUrl();

    Cache provideCache();

    GalleryApi provideGalleryApi();

    void inject(AlbumActivity albumActivity);

    void inject(PhotoActivity photoActivity);
}
