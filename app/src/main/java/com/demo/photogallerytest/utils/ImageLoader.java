package com.demo.photogallerytest.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demo.photogallerytest.R;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class ImageLoader {
    private ImageLoader() throws IllegalAccessException {
        throw new IllegalAccessException("No Instances!");
    }

    /**
     * Shorthand method to load remote image into ImageView with glide
     *
     * @param imageView ImageView into which image has to be loaded
     * @param url       Relative Url path of image
     */
    public static void loadProductImage(ImageView imageView, String url) {
        loadProductImage(imageView, url, true);
    }

    /**
     * Shorthand method to load remote image into ImageView with glide
     *
     * @param imageView ImageView into which image has to be loaded
     * @param url       Relative Url path of image
     * @param thumb     Flag to indicate to load thumbnail version of image.
     */
    public static void loadProductImage(ImageView imageView, String url, boolean thumb) {
        String baseImagePath = "";
        loadImage(imageView, baseImagePath + url, R.drawable.defaultimage);
    }

    /**
     * Shorthand method to load remote image into ImageView with glide
     *
     * @param imageView   ImageView into which image has to be loaded
     * @param url         Relative Url path of image
     * @param placeHolder integer value of place holder
     */
    public static void loadImage(ImageView imageView, String url,
                                 @SuppressWarnings("SameParameterValue") int placeHolder) {
        Glide.with(imageView.getContext().getApplicationContext())
                .load(url)
                .placeholder(placeHolder)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


    }
}
