package com.wit.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class device_data_db extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "device.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "mytable";
    private static final String COLUMN_VERSION = "version";
    private static final String COLUMN_BUILD = "build";
    private static final String COLUMN_FILE_FORMAT = "fileFormat";
    private static final String COLUMN_DEVICE_MODEL = "deviceModel";
    private static final String COLUMN_DEVICE_BRAND = "deviceBrand";
    private static final String COLUMN_DEVICE_BOARD = "deviceBoard";
    private static final String COLUMN_DEVICE_MANUFACTURER = "deviceManufacturer";
    private static final String COLUMN_DEVICE_BASE_OS = "deviceBaseOS";
    private static final String COLUMN_DEVICE_CODENAME = "deviceCodename";
    private static final String COLUMN_DEVICE_RELEASE = "deviceRelease";
    private static final String COLUMN_DEPTH_FRONT_SENSOR = "depthFrontSensor";
    private static final String COLUMN_DEPTH_FRONT_RESOLUTION = "depthFrontResolution";
    private static final String COLUMN_DEPTH_FRONT_RATE = "depthFrontRate";
    private static final String COLUMN_DEPTH_BACK_SENSOR = "depthBackSensor";
    private static final String COLUMN_DEPTH_BACK_RESOLUTION = "depthBackResolution";
    private static final String COLUMN_DEPTH_BACK_RATE = "depthBackRate";

    // ...

    public device_data_db(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_VERSION + " TEXT, " +
                COLUMN_BUILD + " TEXT, " +
                COLUMN_FILE_FORMAT + " TEXT, " +
                COLUMN_DEVICE_MODEL + " TEXT, " +
                COLUMN_DEVICE_BRAND + " TEXT, " +
                COLUMN_DEVICE_BOARD + " TEXT, " +
                COLUMN_DEVICE_MANUFACTURER + " TEXT, " +
                COLUMN_DEVICE_BASE_OS + " TEXT, " +
                COLUMN_DEVICE_CODENAME + " TEXT, " +
                COLUMN_DEVICE_RELEASE + " TEXT, " +
                COLUMN_DEPTH_FRONT_SENSOR + " TEXT, " +
                COLUMN_DEPTH_FRONT_RESOLUTION + " TEXT, " +
                COLUMN_DEPTH_FRONT_RATE + " TEXT, " +
                COLUMN_DEPTH_BACK_SENSOR + " TEXT, " +
                COLUMN_DEPTH_BACK_RESOLUTION + " TEXT, " +
                COLUMN_DEPTH_BACK_RATE + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData() {
        delete_actions();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_VERSION, Build.VERSION.RELEASE);
        values.put(COLUMN_BUILD, Build.DISPLAY);
        values.put(COLUMN_FILE_FORMAT, Build.FINGERPRINT);
        values.put(COLUMN_DEVICE_MODEL, Build.MODEL);
        values.put(COLUMN_DEVICE_BRAND, Build.BRAND);
        values.put(COLUMN_DEVICE_BOARD, Build.BOARD);
        values.put(COLUMN_DEVICE_MANUFACTURER, Build.MANUFACTURER);
        values.put(COLUMN_DEVICE_BASE_OS, Build.VERSION.BASE_OS);
        values.put(COLUMN_DEVICE_CODENAME, Build.VERSION.CODENAME);
        values.put(COLUMN_DEVICE_RELEASE, Build.VERSION.RELEASE);
        values.put(COLUMN_DEPTH_FRONT_SENSOR, Build.DEVICE);
        values.put(COLUMN_DEPTH_FRONT_RESOLUTION, Build.HARDWARE);
        values.put(COLUMN_DEPTH_FRONT_RATE, Build.PRODUCT);
        values.put(COLUMN_DEPTH_BACK_SENSOR, Build.SERIAL);
        values.put(COLUMN_DEPTH_BACK_RESOLUTION, Build.TYPE);
        values.put(COLUMN_DEPTH_BACK_RATE, Build.USER);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    void delete_actions(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

}
