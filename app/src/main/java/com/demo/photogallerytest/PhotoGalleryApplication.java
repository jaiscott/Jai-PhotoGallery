package com.demo.photogallerytest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class PhotoGalleryApplication extends MultiDexApplication {

    private static final String TAG = PhotoGalleryApplication.class.getSimpleName();
    protected AppComponent mAppComponent;
    private static PhotoGalleryApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        instance = this;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static PhotoGalleryApplication getInstance() {
        return instance;
    }
    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
