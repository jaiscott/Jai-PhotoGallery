package com.demo.photogallerytest.ui.gallery;

import android.app.Activity;
import android.gesture.Gesture;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.photogallerytest.R;
import com.demo.photogallerytest.utils.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class SingleImageActivity extends AppCompatActivity {

    static String imageUrl = "", imageTitle = "";

    @Bind(R.id.image_single)
    ImageView imageView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_image);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        imageUrl = bundle.getString("imageurl", "");
        imageTitle = bundle.getString("title", "");

        if (imageTitle != null) {
            toolbarTitle.setText(imageTitle);
        }

        if (imageUrl != null) {
            ImageLoader.loadProductImage(imageView, imageUrl);
        } else {
            imageView.setBackgroundResource(R.drawable.defaultimage);
        }

    }

}
