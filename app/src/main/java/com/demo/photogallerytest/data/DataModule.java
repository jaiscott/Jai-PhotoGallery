package com.demo.photogallerytest.data;

import com.demo.photogallerytest.BuildConfig;
import com.demo.photogallerytest.PhotoGalleryApplication;
import com.demo.photogallerytest.config.Constants;
import com.demo.photogallerytest.data.api.GalleryApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * Created by jaivignesh.m.jt on 6/21/2016.
 */
@Module
public class DataModule {
    private static final String CACHE_CONTROL = "Cache-Control";
    OkHttpClient okHttpClient;

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .build();
        return okHttpClient;
    }

    @Provides
    @Singleton
    public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        System.out.println("message : " + message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!PhotoGalleryApplication.getInstance().hasNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    @Provides
    @Singleton
    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    @Provides
    @Singleton
    public static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(PhotoGalleryApplication.getInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            System.out.println("Could not create Cache! " + e);
        }
        return cache;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(HttpUrl url, Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(url + "")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(provideOkHttpClient())
                .build();
    }


    @Provides
    @Singleton
    public HttpUrl provideBaseUrl() {
        return HttpUrl.parse(Constants.ENDPOINT);
    }


    @Provides
    @Singleton
    public GalleryApi provideGalleryApi(Retrofit retrofit) {
        return retrofit.create(GalleryApi.class);
    }


}
