package com.demo.photogallerytest;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */

@Module
public final class AppModule {
    private final Context mContext;

    public AppModule(Context context) {
        mContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    public Context provideAppContext() {
        return mContext;
    }


}
