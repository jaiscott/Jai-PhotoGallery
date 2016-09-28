package com.demo.photogallerytest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


import com.demo.photogallerytest.data.entities.AlbumResponse;
import com.demo.photogallerytest.data.entities.PhotosResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaivignesh.m.jt on 9/25/2016.
 */
public class MyDatabase extends SQLiteOpenHelper {
    public static final int database_VERSION = 1;

    public static final String DATABASE_NAME = "Album.db";

    public static final String LOGIN_TABLE_ALBUM = "Album";
    public static final String LOGIN_TABLE_PHOTO = "Photo";

    public static final String ALBUMID = "albumid";
    public static final String USERID = "userid";
    public static final String TITLE = "title";
    public static final String PID = "pid";
    public static final String URL = "url";
    public static final String THUMBNAILURL = "thumbnailurl";


    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_ALBUM = "CREATE TABLE " + LOGIN_TABLE_ALBUM + "("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ALBUMID + " TEXT not null," + USERID
                + " TEXT not null ," + TITLE + " TEXT not null" + ")";

        String CREATE_TABLE_PHOTOS = "CREATE TABLE " + LOGIN_TABLE_PHOTO + "("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ALBUMID + " TEXT not null," + PID
                + " TEXT not null ," + TITLE + " TEXT not null," + URL + " TEXT not null," + THUMBNAILURL + " TEXT not null" + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_ALBUM);
        sqLiteDatabase.execSQL(CREATE_TABLE_PHOTOS);
    }

    public void insert_album_details(List<AlbumResponse> albumResponses) {
        Boolean isInsert = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String sqlAlbum = "INSERT INTO " + LOGIN_TABLE_ALBUM + "(" + ALBUMID + "," + USERID + "," + TITLE + ") values(?,?,?)";
        SQLiteStatement stmt = db.compileStatement(sqlAlbum);

        for (int i = 0; i < albumResponses.size(); i++) {
            AlbumResponse albumStore = albumResponses.get(i);
            String albumId = Integer.toString(albumStore.getId());
            String userId = Integer.toString(albumStore.getUserId());
            String title = albumStore.getTitle();
            if (albumId != null) {
                stmt.bindString(1, albumId);
            } else {
                stmt.bindString(1, "");

            }
            if (userId != null) {
                stmt.bindString(2, userId);
            } else {
                stmt.bindString(2, "");

            }
            if (title != null) {
                stmt.bindString(3, title);
            } else {
                stmt.bindString(3, "");
            }

            stmt.execute();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void insert_photos_details(List<PhotosResponse> photosResponse) {
        Boolean isInsert = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String sqlPhots = "INSERT INTO " + LOGIN_TABLE_PHOTO + "(" + ALBUMID + "," + PID + "," + TITLE + "," + URL + "," + THUMBNAILURL +
                ") values(?,?,?,?,?)";
        SQLiteStatement stmt = db.compileStatement(sqlPhots);

        for (int i = 0; i < photosResponse.size(); i++) {
            PhotosResponse photoStore = photosResponse.get(i);

            String albumId = Integer.toString(photoStore.getAlbumId());
            String pid = Integer.toString(photoStore.getId());
            String url = photoStore.getUrl();
            String title = photoStore.getTitle();
            String thumnailUrl = photoStore.getThumbnailUrl();

            if (albumId != null) {
                stmt.bindString(1, albumId);
            } else {
                stmt.bindString(1, "");

            }
            if (pid != null) {
                stmt.bindString(2, pid);
            } else {
                stmt.bindString(2, "");

            }
            if (title != null) {
                stmt.bindString(3, title);
            } else {
                stmt.bindString(3, "");
            }
            if (url != null) {
                stmt.bindString(4, url);
            } else {
                stmt.bindString(4, "");
            }

            if (thumnailUrl != null) {
                stmt.bindString(5, thumnailUrl);
            } else {
                stmt.bindString(5, "");
            }

            stmt.execute();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public List<AlbumResponse> reteriveAlbumValue() {
        List<AlbumResponse> reteriveValue = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM " + LOGIN_TABLE_ALBUM;
        cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                AlbumResponse albumResponse = new AlbumResponse();
                albumResponse.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALBUMID))));
                albumResponse.setUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(USERID))));
                albumResponse.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                reteriveValue.add(albumResponse);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reteriveValue;
    }

    public List<PhotosResponse> reterivePhotoValue() {
        List<PhotosResponse> reterivePhotoValue = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM " + LOGIN_TABLE_PHOTO;
        cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                PhotosResponse photosResponse = new PhotosResponse();
                photosResponse.setAlbumId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALBUMID))));
                photosResponse.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(PID))));
                photosResponse.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                photosResponse.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
                photosResponse.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(THUMBNAILURL)));
                reterivePhotoValue.add(photosResponse);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return reterivePhotoValue;
    }

    public void deleteAlbumRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + LOGIN_TABLE_ALBUM);
        db.close();
    }

    public int getAlbumCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM " + LOGIN_TABLE_ALBUM;
        cursor = db.rawQuery(selectQuery, null);
        int rowCount = cursor.getCount();
        return rowCount;

    }

    public int getPhotoCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM " + LOGIN_TABLE_PHOTO;
        cursor = db.rawQuery(selectQuery, null);
        int rowCount = cursor.getCount();
        return rowCount;

    }

    public void deletePhotoRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + LOGIN_TABLE_PHOTO);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
