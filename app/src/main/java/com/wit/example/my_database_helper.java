package com.wit.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.NavigableMap;

public class my_database_helper extends SQLiteOpenHelper {

    private Context context;

    public static final String DATABASE_NAME = "actions.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "raw_data";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_USER = "User_ID";
    public static final String COLUMN_SEANS_NUMBER = "Unikalniy_nomer_seansa";
    public static final String COLUMN_SURNAME = "Familiya";
    public static final String COLUMN_NAME = "Imya";
    public static final String COLUMN_MIDDLE = "Otchestvo";
    public static final String COLUMN_GENDER = "Pol";
    public static final String COLUMN_BIRTH_DATE = "Data_Rojdeniya";
    public static final String COLUMN_SEANS_DATE = "Data_Seansa";
    public static final String COLUMN_SEANS_PR_1 = "Primechanie_1";
    public static final String COLUMN_SEANS_PR_2 = "Primechanie_2";
    public static final String COLUMN_SEANS_PR_3 = "Primechanie_3";
    public static final String COLUMN_NANOSECOND_OF_ACTION = "Time_3";
    public static final String COLUMN_ACC_X = "AccX";
    public static final String COLUMN_ACC_Y = "AccY";
    public static final String COLUMN_ACC_Z = "AccZ";
    public static final String COLUMN_ACC_ABS = "AccABS";
    public static final String COLUMN_GYRO_X = "GyroX";
    public static final String COLUMN_GYRO_Y = "GyroY";
    public static final String COLUMN_GYRO_Z = "GyroZ";
    public static final String COLUMN_GYRO_ABS = "GyroABS";
    public static final String COLUMN_ANGLE_X = "AngleX";
    public static final String COLUMN_ANGLE_Y = "AngleY";
    public static final String COLUMN_ANGLE_Z = "AngleZ";
    public static final String COLUMN_ANGLE_ABS = "AngleABS";
    public static final String COLUMN_MAG_X = "MagX";
    public static final String COLUMN_MAG_Y = "MagY";
    public static final String COLUMN_MAG_Z = "MagZ";
    public static final String COLUMN_MAG_ABS = "MagABS";
    public static final String COLUMN_TIME = "Time";

    public my_database_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID_USER + " TEXT, " +
                COLUMN_SEANS_NUMBER + " TEXT, " +
                COLUMN_SURNAME + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_MIDDLE + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_BIRTH_DATE + " TEXT, " +
                COLUMN_SEANS_DATE + "  TIME DEFAULT CURRENT_TIME, " +
                COLUMN_SEANS_PR_1 + " TEXT, " +
                COLUMN_SEANS_PR_2 + " TEXT, " +
                COLUMN_SEANS_PR_3 + " TEXT, " +
                COLUMN_NANOSECOND_OF_ACTION + " TEXT, " +
//                COLUMN_ACTION + " TEXT, " +
                COLUMN_ACC_X + " TEXT, " +
                COLUMN_ACC_Y + " TEXT, " +
                COLUMN_ACC_Z + " TEXT, " +
                COLUMN_ACC_ABS + " TEXT, " +

                COLUMN_GYRO_X + " TEXT, " +
                COLUMN_GYRO_Y + " TEXT, " +
                COLUMN_GYRO_Z + " TEXT, " +
                COLUMN_GYRO_ABS + " TEXT, " +

                COLUMN_ANGLE_X + " TEXT, " +
                COLUMN_ANGLE_Y + " TEXT, " +
                COLUMN_ANGLE_Z + " TEXT, " +
                COLUMN_ANGLE_ABS + " TEXT, " +
//
                COLUMN_MAG_X + " TEXT, " +
                COLUMN_MAG_Y + " TEXT, " +
                COLUMN_MAG_Z + " TEXT, " +
                COLUMN_MAG_ABS + " TEXT, " +

                COLUMN_TIME + " TIME DEFAULT CURRENT_TIME);";
        db.execSQL(query) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    void addActions(String seans_number,String id, String surname,String name,String middle,String gender,String birth_date, String seans_date, String action_1 ,String action_2 ,String action_3 , String accX, String accY, String accZ,String AngleX,String AngleY,String AngleZ,String gyroX, String gyroY, String gyroZ,String magX,String magY,String magZ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID_USER , id);
        cv.put(COLUMN_SEANS_NUMBER, seans_number);
        cv.put(COLUMN_SURNAME , surname);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_MIDDLE, middle);
        cv.put(COLUMN_GENDER, gender);
        cv.put(COLUMN_BIRTH_DATE, birth_date);
        cv.put(COLUMN_SEANS_DATE, seans_date);
        cv.put(COLUMN_SEANS_PR_1, action_1);
        cv.put(COLUMN_SEANS_PR_2, action_2);
        cv.put(COLUMN_SEANS_PR_3, action_3);
        cv.put(COLUMN_NANOSECOND_OF_ACTION, getCurrentNano());

        cv.put(COLUMN_ACC_X, accX);
        cv.put(COLUMN_ACC_Y, accY);
        cv.put(COLUMN_ACC_Z, accZ);
        cv.put(COLUMN_ACC_ABS, "");

        cv.put(COLUMN_GYRO_X, gyroX);
        cv.put(COLUMN_GYRO_Y, gyroY);
        cv.put(COLUMN_GYRO_Z, gyroZ);
        cv.put(COLUMN_GYRO_ABS, "");

        cv.put(COLUMN_ANGLE_X, AngleX);
        cv.put(COLUMN_ANGLE_Y, AngleY);
        cv.put(COLUMN_ANGLE_Z, AngleZ);
        cv.put(COLUMN_ANGLE_ABS, "");

        cv.put(COLUMN_MAG_X, magX);
        cv.put(COLUMN_MAG_Y, magY);
        cv.put(COLUMN_MAG_Z, magZ);
        cv.put(COLUMN_MAG_ABS, "");

        cv.put(COLUMN_TIME, getCurrentTime());

        long result = db.insert(TABLE_NAME, null, cv);

        if(result == -1){
            Toast.makeText(context, "Xatolik", Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(context, "Muoffaqiyatli Qo'shildi✅", Toast.LENGTH_SHORT).show();
        }
    }

    void delete_actions(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public ArrayList<String> getAllTableNames(SQLiteDatabase db) {
        ArrayList<String> tableNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(0);
                tableNames.add(tableName);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return tableNames;
    }


    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format(Locale.getDefault(), "%02d/%02d/%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
    }
    private String getCurrentNano() {
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.MINUTE);
        int nano = calendar.get(Calendar.MILLISECOND);
        return String.format(Locale.getDefault(), "%02d:%02d", second, nano);
    }

}
