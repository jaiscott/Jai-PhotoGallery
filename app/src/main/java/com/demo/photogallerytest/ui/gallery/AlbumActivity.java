package com.demo.photogallerytest.ui.gallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.photogallerytest.PhotoGalleryApplication;
import com.demo.photogallerytest.R;
import com.demo.photogallerytest.adapter.AlbumAdapter;
import com.demo.photogallerytest.data.api.GalleryApi;
import com.demo.photogallerytest.data.entities.AlbumResponse;
import com.demo.photogallerytest.database.MyDatabase;
import com.demo.photogallerytest.listeners.OnItemClickListener;
import com.demo.photogallerytest.listeners.RecyclerviewItemclickListener;
import com.demo.photogallerytest.utils.RxUtils;

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
public class AlbumActivity extends AppCompatActivity {

    private static final String TAG = AlbumActivity.class.getSimpleName();
    @Inject
    GalleryApi galleryApi;

    @Bind(R.id.recyclerViewAlbum)
    RecyclerView recyclerView;

    @Bind(R.id.alertLayout)
    RelativeLayout relativeLayout;
    @Bind(R.id.alertTitle)
    TextView alertTV;
    @Bind(R.id.refreshButton)
    ImageButton refreshButton;


    private GridLayoutManager gridLayoutManager;
    private List<AlbumResponse> rowListItem;
    ProgressDialog progressDialog;
    MyDatabase dbh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ((PhotoGalleryApplication) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);

        dbh = new MyDatabase(AlbumActivity.this, MyDatabase.DATABASE_NAME, null, MyDatabase.database_VERSION);
        dbh.getReadableDatabase();
        dbh.getWritableDatabase();

        if (!PhotoGalleryApplication.hasNetwork() && dbh.getAlbumCount() <= 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            alertTV.setText("No Offline Data Found");
            recyclerView.setVisibility(View.GONE);
        }


        gridLayoutManager = new GridLayoutManager(AlbumActivity.this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");

        rowListItem = getAlbumResponse();

        recyclerView.addOnItemTouchListener(new RecyclerviewItemclickListener(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                sendData(rowListItem.get(position).getUserId(), rowListItem.get(position).getId(), rowListItem.get(position).getTitle());
            }
        }));

    }

    public void sendData(int userId, int albumId, String albumName) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        bundle.putInt("albumId", albumId);
        bundle.putString("albumName", albumName);
        Intent i = new Intent(AlbumActivity.this, PhotoActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    public List<AlbumResponse> getAlbumResponse() {
        if (PhotoGalleryApplication.hasNetwork()) {

            progressDialog.show();
            galleryApi.getAlbumResponse()
                    .compose(RxUtils.<List<AlbumResponse>>applySchedulers())
                    .subscribe(new Action1<List<AlbumResponse>>() {
                        @Override
                        public void call(List<AlbumResponse> albumResponse) {
                            progressDialog.dismiss();
                            rowListItem = albumResponse;
                            dbh.deleteAlbumRecord();
                            dbh.insert_album_details(albumResponse);
                            setData(albumResponse);
                            relativeLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            progressDialog.dismiss();
                            if (dbh.getAlbumCount() > 0) {
                                Intent i = new Intent(AlbumActivity.this, AlbumActivity.class);
                                AlbumActivity.this.finish();
                                startActivity(i);
                                Toast.makeText(AlbumActivity.this, "Internet Lost, Entering Offline Mode", Toast.LENGTH_LONG).show();
                            } else {
                                relativeLayout.setVisibility(View.VISIBLE);
                                alertTV.setText("No Offline Data Found");
                                recyclerView.setVisibility(View.GONE);
                                // Toast.makeText(AlbumActivity.this, "Internet Lost, No Offline Data Found", Toast.LENGTH_LONG).show();
                            }

                            Log.d(TAG, "call: " + throwable.toString());
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
            List<AlbumResponse> albumResponses = new ArrayList<>();
            albumResponses = dbh.reteriveAlbumValue();
            if (albumResponses.size() > 0) {
                setData(albumResponses);
                rowListItem = albumResponses;
                Toast.makeText(AlbumActivity.this, R.string.offline_text, Toast.LENGTH_LONG).show();
            }
        }
        return rowListItem;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PhotoGalleryApplication.hasNetwork() && dbh.getAlbumCount() <= 0) {
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
        if (PhotoGalleryApplication.hasNetwork() && dbh.getAlbumCount() <= 0) {
            rowListItem = getAlbumResponse();
        } else {
            Toast.makeText(AlbumActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setData(List<AlbumResponse> albumResponses) {
        AlbumAdapter albumAdapter = new AlbumAdapter(AlbumActivity.this, albumResponses);
        recyclerView.setAdapter(albumAdapter);
    }

}
