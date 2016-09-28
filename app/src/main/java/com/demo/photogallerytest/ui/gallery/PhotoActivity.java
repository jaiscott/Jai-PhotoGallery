package com.demo.photogallerytest.ui.gallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.photogallerytest.PhotoGalleryApplication;
import com.demo.photogallerytest.R;
import com.demo.photogallerytest.adapter.PhotosAdapter;
import com.demo.photogallerytest.data.api.GalleryApi;
import com.demo.photogallerytest.data.entities.AlbumResponse;
import com.demo.photogallerytest.data.entities.PhotosResponse;
import com.demo.photogallerytest.database.MyDatabase;
import com.demo.photogallerytest.listeners.OnItemClickListener;
import com.demo.photogallerytest.listeners.RecyclerviewItemclickListener;
import com.demo.photogallerytest.utils.RxUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = PhotoActivity.class.getSimpleName();

    @Inject
    GalleryApi galleryApi;

    static int userId = 0, albumId = 0;
    String albumName;

    @Bind(R.id.recyclerViewPhotos)
    RecyclerView recyclerView;

    private GridLayoutManager gridLayoutManager;
    private List<PhotosResponse> rowListItem;
    ProgressDialog progressDialog;
    MyDatabase dbh;

    @Bind(R.id.alertLayout)
    RelativeLayout relativeLayout;
    @Bind(R.id.alertTitle)
    TextView alertTV;
    @Bind(R.id.refreshButton)
    ImageButton refreshButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo);
        ((PhotoGalleryApplication) getApplication()).getAppComponent().inject(this);
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

        dbh = new MyDatabase(PhotoActivity.this, MyDatabase.DATABASE_NAME, null, MyDatabase.database_VERSION);
        dbh.getReadableDatabase();
        dbh.getWritableDatabase();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this,
                        permissions, 0);
            }
        }

        if (!PhotoGalleryApplication.hasNetwork() && dbh.getPhotoCount() <= 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            alertTV.setText("No Offline Data Found");
            recyclerView.setVisibility(View.GONE);
        }

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId", 0);
        albumId = bundle.getInt("albumId", 0);
        albumName = bundle.getString("albumName", null);
        if (albumName != null) {
            toolbarTitle.setText(albumName);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");

        getPhotoResponse(userId);

        gridLayoutManager = new GridLayoutManager(PhotoActivity.this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerviewItemclickListener(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                sendData(rowListItem.get(position).getUrl(), rowListItem.get(position).getTitle());
            }
        }));
    }

    public void sendData(String imageUrl, String imageName) {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", imageUrl);
        bundle.putString("title", imageName);
        Intent i = new Intent(PhotoActivity.this, SingleImageActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void getPhotoResponse(int userId) {
        if (PhotoGalleryApplication.hasNetwork()) {
            progressDialog.show();
            galleryApi.getPhotoResponse()
                    .compose(RxUtils.<List<PhotosResponse>>applySchedulers())
                    .subscribe(new Action1<List<PhotosResponse>>() {
                        @Override
                        public void call(List<PhotosResponse> photosResponses) {
                            progressDialog.dismiss();
                            rowListItem = photosResponses;
                            Thread t = new Thread(new Runnable() {
                                public void run() {
                                    dbh.deletePhotoRecord();
                                    dbh.insert_photos_details(rowListItem);
                                }
                            });

                            t.start();
                            relativeLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            setData(photosResponses);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            progressDialog.dismiss();
                            Log.d(TAG, "call: " + throwable.toString());
                            if (!PhotoGalleryApplication.hasNetwork() && dbh.getPhotoCount() <= 0) {
                                relativeLayout.setVisibility(View.VISIBLE);
                                alertTV.setText("No Offline Data Found");
                                recyclerView.setVisibility(View.GONE);
                            }
                            if (throwable instanceof IOException) {
                                if (throwable instanceof ConnectException) {
                                    Log.d(TAG, "ConnectException: no internet");
                                } else if (throwable instanceof SocketTimeoutException) {
                                    Log.d(TAG, "SocketTimeoutException: server down");
                                } else if (throwable instanceof UnknownHostException) {
                                    Log.d(TAG, "UnknownHostException: no internet");
                                } else {
                                    Log.d(TAG, "call: no internet");
                                }
                            } else if (throwable instanceof HttpException) {
                                Log.d(TAG, "HttpException: " + throwable.toString());
                                HttpException httpException = (HttpException) throwable;
                                int statusCode = httpException.code();
                                String message = httpException.message();
                                Log.d(TAG, "HttpException: " + statusCode + "," + message);
                                Response response = httpException.response();
                                Log.d(TAG, "HttpException: " + response.message() + "," + response.code() + "," + response.errorBody().toString() + "," + response.raw().toString());
                            }
                        }
                    });
        } else {
            List<PhotosResponse> photosResponses = new ArrayList<>();
            photosResponses = dbh.reterivePhotoValue();
            if (photosResponses.size() > 0) {
                rowListItem = photosResponses;
                setData(photosResponses);
                Toast.makeText(PhotoActivity.this, R.string.offline_text, Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PhotoActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PhotoGalleryApplication.hasNetwork() && dbh.getPhotoCount() <= 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            alertTV.setText("No Offline Data Found");
            recyclerView.setVisibility(View.GONE);
        } else {
            relativeLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.refreshButton)
    void setRefreshButton() {
        if (PhotoGalleryApplication.hasNetwork() && dbh.getPhotoCount() <= 0) {
            getPhotoResponse(userId);
        } else {
            Toast.makeText(PhotoActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    public void setData(List<PhotosResponse> photosResponses) {
        List<PhotosResponse> filteredValue = new ArrayList<>();
        for (int i = 0; i < photosResponses.size(); i++) {
            if (photosResponses.get(i).getAlbumId() == albumId) {
                filteredValue.add(photosResponses.get(i));
            }
        }
        PhotosAdapter photosAdapter = new PhotosAdapter(PhotoActivity.this, filteredValue);
        recyclerView.setAdapter(photosAdapter);
    }
}
