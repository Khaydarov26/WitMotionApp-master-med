package com.wit.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class db_start_stop_time extends SQLiteOpenHelper {

    Context context;
    private static final String DATABASE_NAME = "times.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "metadatatime";
    public static final String COLUMN_ID = "_id";
    private static final String COLUMN_EVENT = "EVENT";
    private static final String COLUMN_EXPERIMENT = "EXPERIMENT";
    private static final String COLUMN_SYSTEM_TIME = "SYSTEMTIME";
    private static final String COLUMN_SYSTEM_TIME_NEXT = "SYSTEM_TIME_NEXT";

    public db_start_stop_time(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENT + " TEXT, " +
                COLUMN_EXPERIMENT + " TEXT, " +
                COLUMN_SYSTEM_TIME + " TEXT, " +
                COLUMN_SYSTEM_TIME_NEXT + " TEXT );";
        db.execSQL(query) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void start(String val, String experiment, String SystemTime, String SystemTimeNext){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EVENT, val);
        cv.put(COLUMN_EXPERIMENT , experiment);
        cv.put(COLUMN_SYSTEM_TIME, SystemTime);
        cv.put(COLUMN_SYSTEM_TIME_NEXT, SystemTimeNext);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }
    void delete_actions(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
